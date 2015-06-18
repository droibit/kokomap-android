package com.droibit.kokomap.model

import android.content.Context
import com.droibit.easycreator.apply
import com.droibit.easycreator.commit
import com.droibit.easycreator.getDefaultSharedPreferences
import com.droibit.easycreator.getPrivateSharedPreferences
import com.droibit.kokomap.extension.animateCamera
import com.droibit.kokomap.extension.moveCamera
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

/**
 * 地図の中心座標、ズーム率の保存と復元を行う為のクラス
 *
 * @auther kumagai
 * @since 15/06/08
 */
public class MapRestorer constructor(context: Context) {

    companion object {
        val DEFAULT_LAT = "37.7852698098043"
        val DEFAULT_LNG = "134.7852698098043"
        val DEFAULT_ZOOM = 4.244f
        val EXPAND_ZOOM = 14.5f

        private val KEY_LAT = "lat"
        private val KEY_LNG = "lng"
        private val KEY_ZOOM = "zoom"
        private var PREF_NAME = "pref_maps"
    }

    private val mContext = context

    /**
     * 地図の中心座標、ズーム率をプリファレンスへ保存する
     *
     * @param map グーグルマップ
     */
    fun store(map: GoogleMap) = mContext.getPrivateSharedPreferences(PREF_NAME).commit {
        val camera = map.getCameraPosition().toTinyCamera()

        putString(KEY_LAT, camera.latitude.toString())
        putString(KEY_LNG, camera.latLng.toString())
        putFloat(KEY_ZOOM, camera.zoom)
    }

    /**
     * プリファレンスに保存した中心座標、ズーム率を地図に反映される。
     *
     * @return 復元した[TinyCamera]オブジェクト
     */
    fun restore(): TinyCamera {
        val prefs = mContext.getPrivateSharedPreferences(PREF_NAME)
        return TinyCamera(
                    lat  = prefs.getString(KEY_LAT, DEFAULT_LAT).toDouble(),
                    lng  = prefs.getString(KEY_LNG, DEFAULT_LNG).toDouble(),
                    zoom = prefs.getFloat(KEY_ZOOM, DEFAULT_ZOOM)
                )
    }
}