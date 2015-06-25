package com.droibit.kokomap.model

import android.os.Handler
import android.util.Log
import com.droibit.easycreator.sendMessage
import com.droibit.kokomap.BuildConfig
import com.droibit.trimmin.Trimmin
import com.google.android.gms.maps.GoogleMap
import java.util.*

/**
 * GoogleMapsのスナップショットを撮るためのクラス。
 * 撮れたビットマップはメインのアクティビティに送信する
 *
 * Created by kumagai on 2015/06/25.
 */
class Snapshooter constructor(handler: Handler) {
    private val mHandler = handler

    /**
     * GoogleMapsのスナップショットを撮る
     */
    fun snapshot(map: GoogleMap) {
        map.snapshot {
            mHandler.sendMessage {
                what = MSG_SNAPSHOT_TOOK
                obj  = Trimmin.trimSquare(it, true)
            }
        }
    }
}