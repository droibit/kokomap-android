package com.droibit.kokomap.extension

import android.os.Handler
import android.os.Looper
import android.os.Message

/**
 * Create a new instance [Handle].
 */
fun handler(callback: Handler.Callback): Handler {
    return handler(Looper.getMainLooper(), callback)
}

/**
 * Create a new instance [Handle].
 */
fun handler(looper: Looper, callback: Handler.Callback) = Handler(looper, callback)

/**
 * Returns a new [Message] from the global message pool.
 */
inline fun Handler.obtainMessage(init: Message.()->Unit): Message {
    val msg = obtainMessage()
    msg.init()
    return msg
}

/**
 * Pushes a message onto the end of the message queue after all pending messages
 * before the current time.
 */
inline fun Handler.sendMessage(init: Message.()->Unit) {
    val msg = obtainMessage()
    msg.init()
    sendMessage(msg)
}

/**
 * Enqueue a message into the message queue after all pending messages
 * before the absolute time (in milliseconds) <var>uptimeMillis</var>
 */
inline fun Handler.sendMessageAtTime(uptimeMillis: Long, init:Message.()->Unit) {
    val msg = obtainMessage()
    msg.init()
    sendMessageAtTime(msg, uptimeMillis)
}

/**
 * Enqueue a message into the message queue after all pending messages
 * before (current time + delayMillis)
 */
inline fun Handler.sendMessageDelayed(delayMillis: Long, init:Message.()->Unit) {
    val msg = obtainMessage()
    msg.init()
    sendMessageDelayed(msg, delayMillis)
}

fun Handler.postAtTime(uptimeMillis: Long, r: ()->Unit): Boolean = postAtTime(r, uptimeMillis)
fun Handler.postAtTime(uptimeMillis: Long, token: Any?, r: ()->Unit): Boolean = postAtTime(r, token, uptimeMillis)
fun Handler.postDelayed(delayMillis: Long, r: ()->Unit): Boolean = postDelayed(r, delayMillis)
