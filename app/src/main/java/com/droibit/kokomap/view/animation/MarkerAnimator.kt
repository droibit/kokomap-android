package com.droibit.kokomap.view.animation

import android.os.Handler
import android.os.SystemClock
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import com.google.android.gms.maps.model.Marker

public val MSG_DROPPED_MARKER: Int = 1

/**
 * バウンドアニメーション付きで地図にマーカーを落とす。
 * アニメーションが完了したらアクティビティにコールバックする。
 *
 * @auther kumagai
 * @since 15/06/23
 */
class MarkerAnimator constructor(callback: Handler.Callback) {

    private val mHandler = Handler(callback)

    /**
     * アニメーション付きで地図にマーカーを落とす。
     */
    fun drop(marker: Marker, durationMillis: Long) {
        mHandler.post( dropRun {
            val elapsedMillis = SystemClock.uptimeMillis() - startMillis
            val t = Math.max(1f - interpolator.getInterpolation((elapsedMillis.toFloat() / durationMillis)), 0f)

            marker.setAnchor(.5f, 1f + 10f * t)

            if (t > 0.0) {
                // Post again 15ms later.
                mHandler.postDelayed(this, 15L);
            } else {
                mHandler.sendEmptyMessage(MSG_DROPPED_MARKER)
                //marker.showInfoWindow();
            }
        })
    }
}

/**
 * [Runnable] な無名クラスを作成した場合 this参照がクラスを指してしまうのが
 * 解決できなかったため、継承したクラスを作成することにした。
 */
private class DropRunnable constructor (private val drop: DropRunnable.()->Unit): Runnable {
    val interpolator: Interpolator = BounceInterpolator()
    val startMillis: Long = SystemClock.uptimeMillis()

    override fun run() = drop()
}

private fun dropRun(drop: DropRunnable.()->Unit) = DropRunnable(drop)