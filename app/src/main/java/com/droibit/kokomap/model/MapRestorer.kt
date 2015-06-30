package com.droibit.kokomap.model

import android.content.Context
import com.droibit.easycreator.apply
import com.droibit.easycreator.commit
import com.droibit.easycreator.getDefaultSharedPreferences
import com.droibit.easycreator.getPrivateSharedPreferences
import com.droibit.kokomap.extension.animateCamera
import com.droibit.kokomap.extension.moveCamera
import com.droibit.kokomap.model.entity.RestorableCamera
import com.droibit.kokomap.model.entity.toRestorableCamera
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

/**
 * 地図の中心座標、ズーム率の保存と復元を行う為のクラス
 *
 * @auther kumagai
 * @since 15/06/08
 *
 * @constructor 新しいインスタンスを作成します。
 * @param context コンテキスト
 */
class MapRestorer constructor(context: Context) {

    companion object {
        val DEFAULT_LAT = 37.7852698098043
        val DEFAULT_LNG = 134.7852698098043
        val DEFAULT_ZOOM = 4.244f
        val EXPAND_ZOOM  = 14.5f

        private val KEY_LAT   = "lat"
        private val KEY_LNG   = "lng"
        private val KEY_ZOOM  = "zoom"
    }

    private val mContext = context

    /**
     * 地図の中心座標、ズーム率をプリファレンスへ保存する。
     *
     * @param cameraPosition GoogleMapから取得したカメラ情報
     */
    fun store(cameraPosition: CameraPosition) = mContext.getDefaultSharedPreferences().commit {
        val camera = cameraPosition.toRestorableCamera()
        putString(KEY_LAT, camera.latitude.toString())
        putString(KEY_LNG, camera.latLng.toString())
        putFloat(KEY_ZOOM, camera.zoom)
    }

    /**
     * プリファレンスに保存した中心座標、ズーム率を地図に反映される。
     *
     * @return 復元した[RestorableCamera]オブジェクト
     */
    fun restore(): RestorableCamera {
        val prefs = mContext.getDefaultSharedPreferences()
        return RestorableCamera(
                lat = prefs.getString(KEY_LAT, DEFAULT_LAT.toString()).toDouble(),
                lng = prefs.getString(KEY_LNG, DEFAULT_LNG.toString()).toDouble(),
                zoom = prefs.getFloat(KEY_ZOOM, DEFAULT_ZOOM)
        )
    }
}