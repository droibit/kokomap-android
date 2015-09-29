package com.droibit.kokomap

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.annotation.MainThread
import android.support.annotation.VisibleForTesting
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.bindView
import com.droibit.easycreator.intent
import com.droibit.easycreator.postDelayed
import com.droibit.easycreator.startActivity
import com.droibit.kokomap.extension.showSnackbar
import com.droibit.kokomap.fragment.dialog.BalloonDialogFragment
import com.droibit.kokomap.fragment.dialog.PreviewDialogFragment
import com.droibit.kokomap.model.*
import com.droibit.kokomap.utils.ResumeHandler
import com.droibit.kokomap.utils.sendRunnableMessageDelayed
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker


/**
 * 指定位置の地図画像を選択するためのアクティビティ
 *
 * @author kumagai
 * @since 2015/05/12
 */
public class MainActivity : AppCompatActivity(), Handler.Callback {

    private val mMapFragment: SupportMapFragment by lazy {
        getSupportFragmentManager().findFragmentById(R.id.map) as SupportMapFragment
    }
    // [SupportMapFragment]のデリゲート
    private val mMapController: MapController by lazy { MapController(this) }
    private val mFabMenu: FloatingActionMenu by bindView(R.id.fab_menu)
    private val mRootView: View by bindView(R.id.root)

    // バックグランド時にダイアログを操作しないためのハンドラー
    private val mHandler: ResumeHandler = ResumeHandler()
    // 他アプリから起動したかどうか
    private var mLaunchedPickMode = false

    /** {@inheritDoc} */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 他アプリから起動したかどうか
        if (Intent.ACTION_GET_CONTENT == intent?.action) {
            mLaunchedPickMode = true
        }

        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.setTitleTextColor(R.color.gray_dark)
        setSupportActionBar(toolbar)

        mMapFragment.getMapAsync(mMapController)
    }

    /** {@inheritDoc} */
    override fun onResume() {
        super.onResume()

        mHandler.onResume()
    }

    /** {@inheritDoc} */
    override fun onPause() {
        super.onPause()

        mHandler.onPause()
        mMapController.onPause()
    }

    /** {@inheritDoc} */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ACCESS_LOCATION -> {
                if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    mMapController.moveInitialPosition()
                } else {
                    showSnackbar(mRootView, R.string.snackbar_denied_location, Snackbar.LENGTH_LONG)
                }
            }
            REQUEST_WRITE_EXTERNAL_STORAGE -> {
                // パーミッションの許可/拒否にかかわらず保存処理に移行する
                // ※ 拒否されている場合は保存時に必ず失敗するのでいいか?
                mMapController.snapshot(force = true)
            }
        }
    }

    /** {@inheritDoc} */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /** {@inheritDoc} */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings  -> { return startSettingsActivity() }
            R.id.action_satellite -> { return changeMapType(item) }
        }
        return super.onOptionsItemSelected(item)
    }

    /** {@inheritDoc} */
    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_DROPPED_MARKER -> onDropMarkerFinish(msg.obj as Marker, msg.arg1)
            MSG_SNIPPET_CANCEL -> onSnippetCancel()
            MSG_SNIPPET_OK     -> onSnippetOk(msg.obj.toString())
            MSG_SNAPSHOT_TOOK  -> onMapSnapshotTook(msg.obj as Bitmap)
            MSG_USER_COMPLETE  -> onUserCompleted(msg.obj as Bitmap)
            MSG_USER_RETAKE    -> onUserRetake(msg.obj as? Bitmap)
            MSG_SNAPSHOT_SAVED -> onMapSnapshotSaved(msg.obj as? Uri)
        }
        return true
    }

    /**
     * マーカードロップボタンが押下された時の処理
     */
    fun onDropMarker(v: View) {
        val withBalloon = v.id == R.id.fab_balloon
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
        if (mMapController.onMapTypeChange(!item.isChecked)) {
            item.setChecked(!item.isChecked)
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
        showPreviewDialog(snapshot, mLaunchedPickMode)

        mHandler.postDelayed(250L) { mMapController.clearMarker() }
    }

    // 一連の処理が完了した時に呼ばれる処理
    @MainThread
    private fun onUserCompleted(bitmap: Bitmap) = mMapController.saveSnapshot(bitmap)

    // スナップショットの保存が終わった時に呼ばれる処理
    @MainThread
    private fun onMapSnapshotSaved(imgUri: Uri?) {
        var resId = if (imgUri == null) R.string.snackbar_failed_save else R.string.snackbar_saved
        // 通常起動の場合
        if (!mLaunchedPickMode) {
            showSnackbar(mRootView, resId, Snackbar.LENGTH_LONG)
            return
        }

        // スナップショットの保存に失敗した場合
        if (imgUri == null) {
            setResult(Activity.RESULT_CANCELED)
        } else {
            setResult(Activity.RESULT_OK, intent(imgUri))
        }
        finish()
    }

    // ユーザが撮り直しを選択した時に呼ばれる処理
    @MainThread
    private fun onUserRetake(bitmap: Bitmap?) = bitmap?.recycle()

    // 吹き出しスニペット入力用ダイアログを表示する
    @VisibleForTesting
    fun showBalloonDialog() = BalloonDialogFragment().show(supportFragmentManager)
    // スナップショットのプレビューダイアログを表示する
    @VisibleForTesting
    fun showPreviewDialog(snapshot: Bitmap, launchedPickMode: Boolean) = PreviewDialogFragment.newInstance(snapshot, launchedPickMode)
                                                                                              .show(supportFragmentManager)
}
