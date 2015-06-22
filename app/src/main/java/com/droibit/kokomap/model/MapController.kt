package com.droibit.kokomap.model

import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.support.annotation.WorkerThread
import android.util.Log
import com.droibit.easycreator.getLocationManager
import com.droibit.kokomap.BuildConfig
import com.droibit.kokomap.extension.animateCamera
import com.droibit.kokomap.extension.moveCamera
import com.droibit.kokomap.model.MapRestorer
import com.droibit.kokomap.model.entity.RestorableCamera
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlin.properties.Delegates

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
            it.setMapType(satellite.toSatelliteMapType())
            return true
        }
        return false
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
fun Boolean.toSatelliteMapType() = if (this) GoogleMap.MAP_TYPE_SATELLITE else GoogleMap.MAP_TYPE_NORMAL