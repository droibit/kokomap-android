package com.droibit.kokomap.model

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

/**
 * 地図の状態を保存/復元するための最小限のカメラ情報を格納する
 *
 * Created by kumagai on 2015/06/18.
 */
public @data class TinyCamera constructor(lat: Double, lng: Double, zoom: Float) {

    val latLng = LatLng(lat, lng)
    val zoom = zoom

    val latitude:Double get() = latLng.latitude
    val longitude: Double get() = latLng.longitude
}

/**
 * [CameraPosition]からプリファレンスに保存するための情報に変換する
 */
fun CameraPosition.toTinyCamera() = TinyCamera(target.latitude, target.longitude, zoom)