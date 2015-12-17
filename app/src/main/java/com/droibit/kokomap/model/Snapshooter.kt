package com.droibit.kokomap.model

import android.os.Handler
import com.droibit.kokomap.extension.sendMessage
import com.droibit.trimmin.Trimmin
import com.google.android.gms.maps.GoogleMap

/**
 * GoogleMapsのスナップショットを撮るためのクラス。
 * 撮れたビットマップはメインのアクティビティに送信する
 *
 * @author kumagai
 *
 * @constructor 新しいインスタンスを生成する
 * @param handler UIスレッドのハンドラ
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