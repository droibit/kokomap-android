package com.droibit.kokomap.model

import android.content.Context
import com.droibit.easycreator.apply
import com.droibit.easycreator.getDefaultSharedPreferences
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
        val DEFAULT_POSITION = "37.7852698098043,134.7852698098043"
        val DEFAULT_ZOOM = 4.244f
        val EXPAND_ZOOM = 14.5f

        private val KEY_LATLNG =  "latlng"
        private val KEY_ZOOM = "zoom"
    }

    private val mContext = context

    /**
     * 地図の中心座標、ズーム率をプリファレンスへ保存する
     *
     * @param map グーグルマップ
     */
    fun store(map: GoogleMap) {
        mContext.getDefaultSharedPreferences().apply {
            val latlng = "${map.getCameraPosition().target.latitude},${map.getCameraPosition().target.longitude}"
            putString(KEY_LATLNG, latlng)
            putFloat(KEY_ZOOM, map.getCameraPosition().zoom)
        }
    }

    /**
     * プリファレンスに保存した中心座標、ズーム率を地図に反映される。
     *
     * @param map グーグルマップ
     * @param animation 復元する際にアニメーションしながら移動するかどうか
     */
    fun restore(map: GoogleMap, animation: Boolean) {
        val prefs = mContext.getDefaultSharedPreferences()
        val latlng = prefs.getString(KEY_LATLNG, DEFAULT_POSITION).splitBy(",")
        val zoom = prefs.getFloat(KEY_ZOOM, DEFAULT_ZOOM)

        if (animation) {
            map.animateCamera(lat  = latlng[0].toDouble(),
                              lng  = latlng[1].toDouble(),
                              zoom = zoom)
            return
        }
        map.moveCamera(lat  = latlng[0].toDouble(),
                       lng  = latlng[1].toDouble(),
                       zoom = zoom)
    }
}