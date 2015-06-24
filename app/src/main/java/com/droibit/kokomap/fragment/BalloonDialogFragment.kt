package com.droibit.kokomap.fragment

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.droibit.easycreator.compat.getInteger
import com.droibit.easycreator.sendMessage
import com.droibit.kokomap.R
import com.droibit.kokomap.model.MSG_SNIPPET_CANCEL
import com.droibit.kokomap.model.MSG_SNIPPET_OK
import kotlin.properties.Delegates

/**
 * 吹き出しに表示する内容を入力するためのダイアログ
 *
 * @auther kumagai
 * @since 15/06/24
 */
class BalloonDialogFragment: DialogFragment() {

    companion object {
        val TAG_DIALOG = "balloon_dialog"
    }

    private var mPositiveButton: Button by Delegates.notNull()
    private var mBalloonText: EditText by Delegates.notNull()

    private var mHandler: Handler by Delegates.notNull()

    /** {@inheritDoc} */
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        if (activity !is Handler.Callback) {
            throw IllegalStateException("activity must implement Handler.Callback")
        }
        mHandler = Handler(Looper.getMainLooper(), activity)
    }

    /** {@inheritDoc} */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(getActivity(), R.layout.dialog_balloon, null)
        mBalloonText = view.findViewById(R.id.balloon_snippet) as EditText

        val dialog = AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.dialog_title_balloon))
                .setView(view)
                .setPositiveButton(android.R.string.ok) { d, w ->
                    mHandler.sendMessage {
                        what = MSG_SNIPPET_OK
                        obj = mBalloonText.getText().toString()
                    }
                }.setNegativeButton(android.R.string.cancel) { d, w ->
                    mHandler.sendEmptyMessage(MSG_SNIPPET_CANCEL)
                }.create()

        // ダイアログを表示した際にキーボード表示する。
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        return dialog
    }

    /** {@inheritDoc} */
    override fun onResume() {
        super.onResume()

        val dialog = getDialog() as AlertDialog
        mPositiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        mPositiveButton.setEnabled(!TextUtils.isEmpty(mBalloonText.getText()))
    }

    /**
     * ダイアログを表示するためのユーティリティメソッド
     */
    fun show(fm: FragmentManager) {
        setCancelable(false)
        show(fm, TAG_DIALOG)
    }
}