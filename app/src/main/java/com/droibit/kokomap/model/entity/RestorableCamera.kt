package com.droibit.kokomap.model.entity

import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

/**
 * 地図の状態を保存/復元するための最小限のカメラ情報を格納する
 *
 * @author kumagai
 *
 * @constructor 新しいインスタンスを作成します。
 * @param lat 地図の緯度
 * @param lng 地図の経度
 * @param zoom 地図の拡大率
 */
public @data class RestorableCamera constructor(lat: Double, lng: Double, zoom: Float) {

    val latLng = LatLng(lat, lng)
    val zoom = zoom

    val latitude: Double get() = latLng.latitude
    val longitude: Double get() = latLng.longitude
}

/**
 * [CameraPosition]からプリファレンスに保存するための情報に変換する
 */
fun CameraPosition.toRestorableCamera() = RestorableCamera(target.latitude, target.longitude, zoom)