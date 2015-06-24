package com.droibit.kokomap.model

import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.support.annotation.WorkerThread
import android.util.Log
import com.droibit.easycreator.getInteger
import com.droibit.easycreator.getLocationManager
import com.droibit.kokomap.BuildConfig
import com.droibit.kokomap.R
import com.droibit.kokomap.extension.animateCamera
import com.droibit.kokomap.extension.moveCamera
import com.droibit.kokomap.model.MapRestorer
import com.droibit.kokomap.model.entity.RestorableCamera
import com.droibit.kokomap.view.animation.MarkerAnimator
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlin.properties.Delegates

public val MSG_DROPPED_MARKER: Int = 1
public val MSG_SNIPPET_OK: Int = 2
public val MSG_SNIPPET_CANCEL: Int = 3

public val FLOW_MARKER_DROP_ONLY: Int        = 1
public val FLOW_MARKER_DROP_WITH_BALLON: Int = 2

/**
 * GoogleMapと各種モデルクラスを連携させるためのクラス。<br>
 * SupportMapFragmentを継承せずこのクラスにデリゲートする
 *
 * @auther kumagai
 * @since 15/06/07
 *
 * @constructor 新しいインスタンスを生成する
 * @param context コンテキスト
 */
public class MapController constructor(context: Context):
                OnMapReadyCallback, OnCameraChangeListener {

// TODO: 2015/06/21 現在、警告が出ないので意味なし
//    @IntDef(GoogleMap.MAP_TYPE_NORMAL.toLong(), GoogleMap.MAP_TYPE_SATELLITE.toLong())
//    public annotation class Maps

    private val mContext = context
    private val mHandler = Handler(Looper.getMainLooper(), context as Handler.Callback)
    private val mRestorer = MapRestorer(context)
    private val mMarkerAnimator = MarkerAnimator(context as Handler.Callback)

    private var mMap: GoogleMap? = null
    private var mCenterPosition: LatLng? = null

    /** {@inheritDoc} */
    @WorkerThread
    override fun onMapReady(map: GoogleMap) {
        mMap = map

        map.setMyLocationEnabled(true)
        map.setOnCameraChangeListener(this)
        moveInitialPosition(map)
    }

    /** {@inheritDoc} */
    override fun onCameraChange(camera: CameraPosition) {
        Log.d(BuildConfig.BUILD_TYPE, "lat: ${camera.target.latitude}, lon: ${camera.target.longitude}, zoom: ${camera.zoom}")

        mCenterPosition = camera.target
    }

    /**
     * ライフサイクルの#onResume 時に呼ぶ
     */
    fun onResume() {
    }

    /**
     * ライフサイクルの#onPause 時に呼ぶ
     */
    fun onPause() {
        mMap?.let { mRestorer.store(it.getCameraPosition()) }
    }

    /**
     * 地図の種類を変更する際に呼ばれる処理
     *
     * @param satellite trueの場合は衛星写真に変更する
     * @return trueの場合は変更、falseの場合は変更していない（地図の準備がまだ）
     */
    fun onMapTypeChanged(satellite: Boolean): Boolean {
        mMap?.let {
            it.setMapType(satellite.toMapType())
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
                                        .position(it.getCameraPosition().target)
                                        .draggable(false)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
            mMarkerAnimator.drop(
                    marker         = marker,
                    flowType       = withBalloon.toDropFlow(),
                    durationMillis = mContext.getInteger(R.integer.bouce_animation_millis).toLong()
            )
        }
    }

    /**
     * アニメーションが終了して追加が完了したときに呼ばれる処理
     */
    fun onDropMarkerFinish(marker: Marker) {

    }

    /**
     * 地図上からマーカーを削除します。
     */
    fun clearMarker() {
        mMap?.let { it.clear() }
    }

    /**
     * カメラを初期位置に移動する。<br>
     *
     * 直近の位置情報がある場合はそれを使用し、
     * とれなかった場合は最後に表示していた地図の状態を復元する。
     */
    private fun moveInitialPosition(map: GoogleMap) {
        val lm = mContext.getLocationManager()
        val ll = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

        var camera = if (ll != null) {
            RestorableCamera(ll.getLatitude(), ll.getLongitude(), MapRestorer.Companion.EXPAND_ZOOM)
                     } else {
                        mRestorer.restore()
                     }
        map.moveCamera(camera.latitude, camera.longitude, camera.zoom)
    }
}

// ブール値からマップの種類に変換する
fun Boolean.toMapType() = if (this) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL
// ブール値からマーカー追加のフローを取得する
fun Boolean.toDropFlow() = if (this) FLOW_MARKER_DROP_WITH_BALLON else FLOW_MARKER_DROP_ONLY