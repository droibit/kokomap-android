package com.droibit.kokomap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar

/**
 * 設定画面を表示するためのアクティビティ
 *
 * @auther kumagai
 * @since 15/06/22
 */
public class SettingsActivity: AppCompatActivity() {

    /** {@inheritDoc} */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_settings)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitleTextColor(R.color.gray_dark)

        setSupportActionBar(toolbar)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true)
    }
}