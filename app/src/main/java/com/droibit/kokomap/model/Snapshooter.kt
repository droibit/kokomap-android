package com.droibit.kokomap.model

import android.os.Handler
import com.google.android.gms.maps.GoogleMap

/**
 * GoogleMapsのスナップショットを撮るためのクラス
 *
 * Created by kumagai on 2015/06/25.
 */
class Snapshooter constructor(handler: Handler) {
    private val mHandler = handler

    /**
     * GoogleMapsのスナップショットを撮る
     */
    fun snapshot(map: GoogleMap) {
    }
}