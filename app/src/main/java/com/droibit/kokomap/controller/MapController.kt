package com.droibit.kokomap.controller

import android.content.Context
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.droibit.easycreator.getLocationManager
import com.droibit.kokomap.BuildConfig
import com.droibit.kokomap.extension.animateCamera
import com.droibit.kokomap.extension.moveCamera
import com.droibit.kokomap.model.MapRestorer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlin.properties.Delegates

/**
 *
 * @auther kumagai
 * @since 15/06/07
 *
 * @constructor 新しいインスタンスを生成する
 * @param context コンテキスト
 */
public class MapController constructor(context: Context):
                OnMapReadyCallback, OnCameraChangeListener {


    private val mContext = context
    private val mHandler = Handler(Looper.getMainLooper(), context as Handler.Callback)
    private val mRestorer = MapRestorer(context)

    private var mMap: GoogleMap? = null
    private var mCenterPosition: LatLng? = null

    /** {@inheritDoc} */
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
        mMap?.let { mRestorer.store(it) }
    }

    /**
     * カメラを初期位置に移動する。
     * 直近の位置情報あある場合はそれを使用する。
     * とれなかった場合は最後に表示していた地図の状態を復元する。
     */
    private fun moveInitialPosition(map: GoogleMap) {
        val lm = mContext.getLocationManager()
        val lastLocation = lm?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (lastLocation != null) {
            map.moveCamera(lat  = lastLocation.getLatitude(),
                           lng  = lastLocation.getLongitude(),
                           zoom = MapRestorer.EXPAND_ZOOM)
            return
        }
        mRestorer.restore(map, animation = true)
    }
}
