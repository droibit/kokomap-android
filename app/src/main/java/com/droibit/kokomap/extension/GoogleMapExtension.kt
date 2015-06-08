package com.droibit.kokomap.extension

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

/**
 * [GoogleMap]の拡張メソッドを定義する
 *
 * @auther kumagai
 * @since 15/06/07
 */

/**
 * 緯度経度を指定してカメラを移動する
 */
fun GoogleMap.moveCamera(lat: Double, lng: Double) = moveCamera(LatLng(lat, lng))

/**
 * 緯度経度を指定してカメラを移動する
 */
fun GoogleMap.moveCamera(latlng: LatLng) = moveCamera(CameraUpdateFactory.newLatLng(latlng))

/**
 * 緯度経度、ズーム率を指定してカメラを移動する
 */
fun GoogleMap.moveCamera(lat: Double, lng: Double, zoom: Float) = moveCamera(LatLng(lat, lng), zoom)

/**
 * 緯度経度、ズーム率を指定してカメラを移動する
 */
fun GoogleMap.moveCamera(latlng: LatLng, zoom: Float) = moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom))

/**
 * 緯度経度を指定してアニメーションしながらカメラを移動する
 */
fun GoogleMap.animateCamera(lat: Double, lng: Double) = animateCamera((LatLng(lat, lng)))

/**
 * 緯度経度を指定してアニメーションしながらカメラを移動する
 */
fun GoogleMap.animateCamera(latlng: LatLng) = animateCamera(CameraUpdateFactory.newLatLng(latlng))

/**
 * 緯度経度、ズーム率を指定してアニメーションしながらカメラを移動する
 */
fun GoogleMap.animateCamera(lat: Double, lng: Double, zoom: Float) = animateCamera(LatLng(lat, lng), zoom)

/**
 * 緯度経度、ズーム率を指定してアニメーションしながらカメラを移動する
 */
fun GoogleMap.animateCamera(latlng: LatLng, zoom: Float) = animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, zoom))