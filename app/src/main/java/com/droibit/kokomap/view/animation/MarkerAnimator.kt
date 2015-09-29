package com.droibit.kokomap.view.animation

import android.os.Handler
import android.os.SystemClock
import android.view.animation.BounceInterpolator
import android.view.animation.Interpolator
import com.droibit.easycreator.sendMessage
import com.droibit.kokomap.model.MSG_DROPPED_MARKER
import com.google.android.gms.maps.model.Marker

/**
 * バウンドアニメーション付きで地図にマーカーを落とす。
 * アニメーションが完了したらアクティビティにコールバックする。
 *
 * @auther kumagai
 * @since 15/06/23
 */
class MarkerAnimator constructor(callback: Handler.Callback) {

    var isAnimating: Boolean = false
        private set

    private val mHandler = Handler(callback)

    /**
     * アニメーション付きで地図にマーカーを落とす。
     *
     * @param target 対象のマーカー
     * @param flowType マーカー追加後のフローの種類
     * @param durationMillis アニメーション時間
     */
    fun drop(target: Marker, flowType: Int, durationMillis: Long) {
        isAnimating = true

        mHandler.post {
            val elapsedMillis = SystemClock.uptimeMillis() - startMillis
            val t = Math.max(1f - interpolator.getInterpolation((elapsedMillis.toFloat() / durationMillis)), 0f)
            target.setAnchor(.5f, 1f + 10f * t)

            if (t > 0f) {
                mHandler.postDelayed(this, 15L);
                return@post
            }
            target.setAnchor(.5f, 1f)
            mHandler.sendMessage {
                what = MSG_DROPPED_MARKER
                obj  = target
                arg1 = flowType
            }
            isAnimating = false
        }
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
private fun Handler.post(run: DropRunnable.()->Unit) = post(DropRunnable(run))