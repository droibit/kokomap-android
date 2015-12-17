package com.droibit.kokomap.utils

import android.os.Handler
import android.os.Message
import android.util.Log
import com.droibit.kokomap.BuildConfig
import java.util.*

/**
 * ActivityもしくはFragmentのレジューム時に処理を遅延させるためのハンドラ。
 * アプリがバックグランドにある状態でダイアログフラグメントを操作すると例外が
 * 発生するのでその対策として使用する。
 *
 * 参考:
 * [StackOverFlow](http://stackoverflow.com/questions/8040280/how-to-handle-handler-messages-when-activity-fragment-is-paused)
 *
 * @author kumagai
 */
public open class ResumeHandler(): Handler() {

    private val mMessageQueue = ArrayList<Message>()
    private var mPaused = false

    /**
     * ライフサイクルのレジュームの際にメッセージがあれば処理する。
     */
    public fun onResume() {
        mPaused = false

        if (!mMessageQueue.isEmpty()) {
            mMessageQueue.forEach { sendMessage(it) }
            mMessageQueue.clear()
        }
    }

    /**
     * ライフサイクルのポーズ時に必ず呼ぶ。
     */
    public fun onPause() {
        mPaused = true
    }

    /** {@inheritDoc} */
    override fun handleMessage(msg: Message) {
        if (mPaused) {
            if (storeMessage(msg)) {
                val copiedMsg = Message()
                copiedMsg.copyFrom(msg)
                mMessageQueue.add(copiedMsg)
            }
            return
        }
        processMessage(msg)
    }

    /**
     * レジューム時に処理する[Message]を保持するかどうか。
     */
    open fun storeMessage(msg: Message): Boolean {
        return true
    }

    /**
     * レジューム時に保持していたメッセージを処理する。
     */
    open fun processMessage(msg: Message) {
        try {
            (msg.obj as Runnable).run()
        } catch (e: ClassCastException) {
            Log.e(BuildConfig.BUILD_TYPE, "", e)
        }
    }
}

public inline fun  ResumeHandler.sendRunnableMessageDelayed(delayMillis: Long, crossinline run: ()->Unit) {
    val msg = obtainMessage()
    msg.obj = Runnable { run() }
    sendMessageDelayed(msg, delayMillis)
}
