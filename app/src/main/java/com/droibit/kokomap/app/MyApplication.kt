package com.droibit.kokomap.app

import android.app.Application
import com.squareup.leakcanary.LeakCanary

/**
 * [LeakCanary]を使用するために使用します。
 *
 * Created by kumagai on 2015/06/26.
 */
public class MyApplication: Application() {


    override fun onCreate() {
        super.onCreate()

        LeakCanary.install(this);
    }
}