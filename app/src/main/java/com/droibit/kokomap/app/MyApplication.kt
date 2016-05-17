package com.droibit.kokomap.app

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * [LeakCanary]を使用するために使用します。
 *
 * @author kumagai
 */
class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        LeakCanary.install(this);
    }
}