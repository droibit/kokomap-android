package com.droibit.kokomap

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import butterknife.bindView
import com.droibit.easycreator.postDelayed
import com.droibit.easycreator.sendMessageDelayed
import com.droibit.easycreator.showToast
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.maps.SupportMapFragment
import kotlin.properties.Delegates
import com.droibit.easycreator.startActivity
import com.droibit.kokomap.fragment.BalloonDialogFragment
import com.droibit.kokomap.model.*
import com.droibit.kokomap.utils.ResumeHandler
import com.google.android.gms.maps.model.Marker


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
    private val mMapController: MapController by Delegates.lazy { MapController(this) }
    private val mFabMenu: FloatingActionMenu by bindView(R.id.fab_menu)

    // バックグランド時にダイアログを操作しないためのハンドラー
    private val mHandler: ResumeHandler = ResumeHandler()

    /** {@inheritDoc} */
    override fun onCreate(savedInstanceState: Bundle?) {
        super<AppCompatActivity>.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitleTextColor(R.color.gray_dark)
        setSupportActionBar(toolbar)

        mMapFragment.getMapAsync(mMapController)
    }

    /** {@inheritDoc} */
    override fun onResume() {
        super<AppCompatActivity>.onResume()

        mHandler.resume()
    }

    /** {@inheritDoc} */
    override fun onPause() {
        super<AppCompatActivity>.onPause()

        mHandler.pause()
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
        when (item.getItemId()) {
            R.id.action_settings  -> { return startSettingsActivity() }
            R.id.action_satellite -> { return changeMapType(item) }
        }
        return super<AppCompatActivity>.onOptionsItemSelected(item)
    }

    /** {@inheritDoc} */
    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_DROPPED_MARKER -> onDropMarkerFinish(msg.obj as Marker, msg.arg1)
            MSG_SNIPPET_CANCEL -> mMapController.clearMarker()
        }
        return true
    }

    /**
     * マーカードロップボタンが押下された時の処理
     */
    fun onDropMarker(v: View) {
        val withBalloon = v.getId() == R.id.fab_balloon
        mMapController.onDropMarker(withBalloon)

        mFabMenu.close(true)
    }

    // 設定画面を表示する
    private fun startSettingsActivity(): Boolean {
        startActivity<SettingsActivity>()
        return true
    }

    // GoogleMapが表示する地図の種類を変更する
    private fun changeMapType(item: MenuItem): Boolean {
        if (mMapController.onMapTypeChanged(!item.isChecked())) {
            item.setChecked(!item.isChecked())
            return true
        }
        return false
    }

    // 吹き出しの追加が完了した時に呼ばれる処理
    private fun onDropMarkerFinish(marker: Marker, flowType: Int) {
        if (flowType == FLOW_MARKER_DROP_ONLY) {
            mMapController.onDropMarkerFinish(marker)
            return
        }

        // 吹き出し内容を入力するためのダイアログを表示する
        mHandler.sendMessageDelayed(250L) {
            obj = Runnable { BalloonDialogFragment().show(getSupportFragmentManager()) }
        }
    }
}
