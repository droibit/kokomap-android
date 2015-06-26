package com.droibit.kokomap

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.annotation.MainThread
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import butterknife.bindView
import com.droibit.easycreator.*
import com.droibit.kokomap.extension.showSnackbar
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.maps.SupportMapFragment
import kotlin.properties.Delegates
import com.droibit.kokomap.fragment.dialog.BalloonDialogFragment
import com.droibit.kokomap.fragment.dialog.PreviewDialogFragment
import com.droibit.kokomap.model.*
import com.droibit.kokomap.utils.ResumeHandler
import com.droibit.kokomap.utils.sendRunnableMessageDelayed
import com.droibit.kokomap.utils.sendRunnableMessage
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
    private val mRootView: View by bindView(R.id.root)

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

        mHandler.onResume()
    }

    /** {@inheritDoc} */
    override fun onPause() {
        super<AppCompatActivity>.onPause()

        mHandler.onPause()
        mMapController.onPause()
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
            MSG_SNIPPET_CANCEL -> onSnippetCancel()
            MSG_SNIPPET_OK     -> onSnippetOk(msg.obj.toString())
            MSG_SNAPSHOT_TOOK  -> onMapSnapshotTook(msg.obj as Bitmap)
            MSG_USER_COMPLETE  -> onUserCompleted(msg.obj as Bitmap)
            MSG_USER_RETAKE    -> onUserRetake(msg.obj as Bitmap)
            MSG_SNAPSHOT_SAVED -> onMapSnapshotSaved(msg.obj as Boolean)
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
        if (mMapController.onMapTypeChange(!item.isChecked())) {
            item.setChecked(!item.isChecked())
            return true
        }
        return false
    }

    // 吹き出しの追加が完了した時に呼ばれる処理
    @MainThread
    private fun onDropMarkerFinish(marker: Marker, flowType: Int) {
        mMapController.marker = marker

        // 吹き出しを表示しない場合はスナップショットへ移る
        if (flowType == FLOW_MARKER_DROP_ONLY) {
            mMapController.snapshot()
            return
        }
        mHandler.sendRunnableMessageDelayed(250L) { showBalloonDialog() }
    }

    // スニペットの入力がキャンセルされた時に呼ばれる処理
    @MainThread
    private fun onSnippetCancel() = mMapController.clearMarker()

    // スニペットの入力が完了した時に呼ばれる処理
    @MainThread
    private fun onSnippetOk(text: String) = mMapController.onSnippetInputted(text)

    // 地図のスナップショットを撮りえ終えた際に呼ばれる処理
    @MainThread
    private fun onMapSnapshotTook(snapshot: Bitmap) {
        showPreviewDialog(snapshot)

        mHandler.postDelayed(250L) { mMapController.clearMarker() }
    }

    // 一連の処理が完了した時に呼ばれる処理
    @MainThread
    private fun onUserCompleted(bitmap: Bitmap) = mMapController.saveSnapshot(bitmap)

    // スナップショットの保存が終わった時に呼ばれる処理
    @MainThread
    private fun onMapSnapshotSaved(hasError: Boolean) {
        var resId = if (!hasError) R.string.snackbar_saved else R.string.snackbar_failed_save
        showSnackbar(mRootView, resId, Snackbar.LENGTH_LONG)
    }

    // ユーザが撮り直しを選択した時に呼ばれる処理
    @MainThread
    private fun onUserRetake(bitmap: Bitmap) = bitmap.recycle()

    // 吹き出しスニペット入力用ダイアログを表示する
    private fun showBalloonDialog() = BalloonDialogFragment().show(getSupportFragmentManager())
    // スナップショットのプレビューダイアログを表示する
    private fun showPreviewDialog(snapshot: Bitmap) = PreviewDialogFragment.newInstance(snapshot, false).show(getSupportFragmentManager())
}
