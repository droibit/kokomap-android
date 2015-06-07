package com.droibit.kokomap

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import butterknife.bindView
import com.droibit.easycreator.showToast
import com.droibit.kokomap.controller.MapController
import com.google.android.gms.maps.SupportMapFragment
import kotlin.properties.Delegates


/**
 * 指定位置の地図画像を選択するためのアクティビティ
 *
 * @author kumagai
 * @since 2015/05/12
 */
public class MainActivity : AppCompatActivity(), Handler.Callback {

    private val mMapFragment: SupportMapFragment by Delegates.lazy {
        getSupportFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
    }
    // [SupportMapFragment]のデリゲート
    private val mMapController: MapController by Delegates.lazy {
        MapController(this)
    }
    private val mDoneButton: FloatingActionButton by bindView(R.id.fab)

    /** {@inheritDoc} */
    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitleTextColor(R.color.darker_gray)
        setSupportActionBar(toolbar)

        mMapFragment.getMapAsync(mMapController)

        mDoneButton.setOnClickListener { onClickDone(it) }
    }

    /** {@inheritDoc} */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu)
        return true
    }

    /** {@inheritDoc} */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.getItemId()
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }
        return super<AppCompatActivity>.onOptionsItemSelected(item)
    }

    /** {@inheritDoc} */
    override fun handleMessage(msg: Message): Boolean {
        throw UnsupportedOperationException()
    }

    // 完了ボタンが押下された時の処理
    private fun onClickDone(v: View) {
        showToast(this, "DONE!!", Toast.LENGTH_SHORT)
    }
}
