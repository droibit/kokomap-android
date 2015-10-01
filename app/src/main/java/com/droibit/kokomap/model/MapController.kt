package com.droibit.kokomap.model

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.support.annotation.WorkerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import com.droibit.easycreator.getInteger
import com.droibit.easycreator.getLocationManager
import com.droibit.easycreator.postDelayed
import com.droibit.easycreator.sendMessage
import com.droibit.kokomap.BuildConfig
import com.droibit.kokomap.R
import com.droibit.kokomap.extension.moveCamera
import com.droibit.kokomap.model.entity.RestorableCamera
import com.droibit.kokomap.view.animation.MarkerAnimator
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import java.io.IOException

const val MSG_DROPPED_MARKER:  Int = 1
const val MSG_SNIPPET_OK:      Int = 2
const val MSG_SNIPPET_CANCEL:  Int = 3
const val MSG_SNAPSHOT_TOOK:   Int = 4
const val MSG_USER_COMPLETE:   Int = 5 // ユーザが完了を選択した
const val MSG_USER_RETAKE:     Int = 6
const val MSG_SNAPSHOT_SAVED:  Int = 7

const val FLOW_MARKER_DROP_ONLY:         Int = 1
const val FLOW_MARKER_DROP_WITH_BALLOON: Int = 2

const val REQUEST_ACCESS_LOCATION:        Int = 1
const val REQUEST_WRITE_EXTERNAL_STORAGE: Int = 2

/**
 * GoogleMapと各種モデルクラスを連携させるためのクラス。<br>
 * SupportMapFragmentを継承せずこのクラスにデリゲートする
 *
 * @author kumagai
 *
 * @constructor 新しいインスタンスを生成する
 * @param context コンテキスト
 */
class MapController constructor(context: Context):
                OnMapReadyCallback, OnCameraChangeListener {

    private val mContext = context
    private val mHandler = Handler(Looper.getMainLooper(), context as Handler.Callback)
    private val mRestorer = MapRestorer(context)
    private val mSnapshooter = Snapshooter(mHandler)
    private val mMarkerAnimator = MarkerAnimator(context as Handler.Callback)
    private val mBitmapWriter = BitmapWriter(context)

    private var mMap: GoogleMap? = null
    private var mCenterPosition: LatLng? = null

    // 中心に表示したマーカー
    var marker: Marker? = null

    /** {@inheritDoc} */
    @WorkerThread
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        map.isMyLocationEnabled = true
        map.setOnCameraChangeListener(this)

        if (!requestAccessLocationIfNeed()) {
            moveInitialPosition()
        }
    }

    /** {@inheritDoc} */
    override fun onCameraChange(camera: CameraPosition) {
        if (BuildConfig.DEBUG) {
            Log.d(BuildConfig.BUILD_TYPE, "lat: ${camera.target.latitude}, lon: ${camera.target.longitude}, zoom: ${camera.zoom}")
        }
        mCenterPosition = camera.target
    }

    /**
     * ライフサイクルの#onPause 時に呼ぶ
     */
    fun onPause() {
        mMap?.let { mRestorer.store(it.cameraPosition) }
    }

    /**
     * 地図の種類を変更する際に呼ばれる処理
     *
     * @param satellite trueの場合は衛星写真に変更する
     * @return trueの場合は変更、falseの場合は変更していない（地図の準備がまだ）
     */
    fun onMapTypeChange(satellite: Boolean): Boolean {
        mMap?.let {
            it.mapType = satellite.toMapType()
            return true
        }
        return false
    }

    /**
     * 地図にマーカーを落とす際に呼ばれる処理。
     * ドロップアニメーション中はマーカーは追加できないようにする
     *
     * @param withBalloon マーカーに吹き出しをつけるかどうか
     */
    fun onDropMarker(withBalloon: Boolean) {
        if (mMarkerAnimator.isAnimating) {
            return
        }

        mMap?.let {
            val marker = it.addMarker(MarkerOptions()
                                        .position(it.cameraPosition.target)
                                        .draggable(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            mMarkerAnimator.drop(
                    target         = marker,
                    flowType       = withBalloon.toDropFlow(),
                    durationMillis = mContext.getInteger(R.integer.bouce_animation_millis).toLong()
            )
        }
    }

    /**
     * 吹き出しのスニペットの内容が入力されたら呼ばれる処理
     */
    fun onSnippetInputted(text: String) {
        // スニペットが入力されたら地図上で表示します。
        marker?.let {
            it.title = mContext.getString(R.string.marker_title)
            it.snippet = text
            it.showInfoWindow()
        }
        mHandler.postDelayed(300L) { snapshot() }
    }

    /**
     * 地図のスナップショットを撮る
     */
    fun snapshot(force: Boolean = false) {
        if (force || !requestWriteExternalStorageIfNeed()) {
            mSnapshooter.snapshot(mMap!!)
        }
    }

    /**
     * スナップショットを内部ストレージに保存する
     */
    fun saveSnapshot(snapshot: Bitmap) {
        val task = object: AsyncTask<Bitmap, Void, Uri?>() {
            override fun doInBackground(vararg params: Bitmap): Uri? {
                try {
                    val imgPath = mBitmapWriter.write(params.first())
                    return mBitmapWriter.registrant.register(imgPath)
                } catch (e: IOException) {
                    if (BuildConfig.DEBUG) Log.e(BuildConfig.BUILD_TYPE, "bitmap save error: ", e)
                }
                return null
            }

            override fun onPostExecute(result: Uri?) {
                mHandler.sendMessage {
                    what = MSG_SNAPSHOT_SAVED
                    obj  = result
                }
            }
        }
        task.execute(snapshot)
    }

    /**
     * 地図上からマーカーを削除します。
     */
    fun clearMarker() {
        mMap?.clear()
        marker = null
    }

    /**
     * カメラを初期位置に移動する。<br>
     *
     * 直近の位置情報がある場合はそれを使用し、
     * とれなかった場合は最後に表示していた地図の状態を復元する。
     */
    fun moveInitialPosition() {
        val lm = mContext.getLocationManager()
        val ll = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        var camera = if (ll != null) {
                        RestorableCamera(ll.latitude, ll.longitude, MapRestorer.Companion.EXPAND_ZOOM)
                     } else {
                        mRestorer.restore()
                     }
        mMap!!.moveCamera(camera.latitude, camera.longitude, camera.zoom)
    }

    /**
     * ロケーションアクセスのパーミッションが許可されていない場合は要求する
     *
     * @return trueの場合は要求、falseの場合は要求不要
     */
    private fun requestAccessLocationIfNeed(): Boolean {
        if (grantedPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            return false
        }
        ActivityCompat.requestPermissions(mContext as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                            REQUEST_ACCESS_LOCATION)
        return true
    }

    /**
     * 外部ストレージへの書き込みが許可されていない場合は要求する
     *
     * @return trueの場合は要求、falseの場合は要求不要
     */
    private fun requestWriteExternalStorageIfNeed(): Boolean {
        if (grantedPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            return false
        }
        ActivityCompat.requestPermissions(mContext as Activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_WRITE_EXTERNAL_STORAGE)
        return true
    }

    private fun grantedPermission(permission: String) =
            ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED
}

// ブール値からマップの種類に変換する
fun Boolean.toMapType() = if (this) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL
// ブール値からマーカー追加のフローを取得する
fun Boolean.toDropFlow() = if (this) FLOW_MARKER_DROP_WITH_BALLOON else FLOW_MARKER_DROP_ONLY