package com.droibit.kokomap.model

import android.content.Context
import android.preference.PreferenceManager.getDefaultSharedPreferences
import com.droibit.kokomap.model.entity.RestorableCamera
import com.droibit.kokomap.model.entity.toRestorableCamera
import com.google.android.gms.maps.model.CameraPosition

/**
 * 地図の中心座標、ズーム率の保存と復元を行う為のクラス
 *
 * @author kumagai
 *
 * @constructor 新しいインスタンスを作成します。
 * @param context コンテキスト
 */
class MapRestorer constructor(context: Context) {

    companion object {
        const val DEFAULT_LAT = 37.7852698098043
        const val DEFAULT_LNG = 134.7852698098043
        const val DEFAULT_ZOOM = 4.244f
        const val EXPAND_ZOOM  = 14.5f

        const private val KEY_LAT   = "lat"
        const private val KEY_LNG   = "lng"
        const private val KEY_ZOOM  = "zoom"
    }

    private val mContext = context

    /**
     * 地図の中心座標、ズーム率をプリファレンスへ保存する。
     *
     * @param cameraPosition GoogleMapから取得したカメラ情報
     */
    fun store(cameraPosition: CameraPosition): Boolean {
        val editor = getDefaultSharedPreferences(mContext).edit().apply() {
            val camera = cameraPosition.toRestorableCamera()
            putString(KEY_LAT, camera.latitude.toString())
            putString(KEY_LNG, camera.latLng.toString())
            putFloat(KEY_ZOOM, camera.zoom)
        }
        return editor.commit()
    }

    /**
     * プリファレンスに保存した中心座標、ズーム率を地図に反映される。
     *
     * @return 復元した[RestorableCamera]オブジェクト
     */
    fun restore(): RestorableCamera {
        val prefs = getDefaultSharedPreferences(mContext)
        return RestorableCamera(
                lat = prefs.getString(KEY_LAT, DEFAULT_LAT.toString()).toDouble(),
                lng = prefs.getString(KEY_LNG, DEFAULT_LNG.toString()).toDouble(),
                zoom = prefs.getFloat(KEY_ZOOM, DEFAULT_ZOOM)
        )
    }
}