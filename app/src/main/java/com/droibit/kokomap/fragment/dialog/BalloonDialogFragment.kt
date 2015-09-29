package com.droibit.kokomap.fragment.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import com.droibit.easycreator.sendMessage
import com.droibit.kokomap.R
import com.droibit.kokomap.model.MSG_SNIPPET_CANCEL
import com.droibit.kokomap.model.MSG_SNIPPET_OK
import kotlin.properties.Delegates

/**
 * 吹き出しに表示する内容を入力するためのダイアログ。
 * キャンセルボタン以外ではダイアログを閉じれなくしている
 *
 * @auther kumagai
 * @since 15/06/24
 */
class BalloonDialogFragment: DialogFragment(), DialogInterface.OnClickListener {

    companion object {
        val TAG_DIALOG = "balloon_dialog"
    }

    private val mContentWatcher = object: TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

        override fun afterTextChanged(s: Editable?) {
            // スニペットの内容が空の場合はOKボタンを押せなくする
            mPositiveButton.isEnabled = !TextUtils.isEmpty(s?.toString())
        }
    }
    private var mPositiveButton: Button by Delegates.notNull()
    private var mBalloonText: EditText by Delegates.notNull()

    private var mHandler: Handler by Delegates.notNull()

    /** {@inheritDoc} */
    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)

        if (activity !is Handler.Callback) {
            throw IllegalStateException("Activity must implements Handler.Callback")
        }
        mHandler = Handler(Looper.getMainLooper(), activity)
    }

    /** {@inheritDoc} */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.dialog_balloon, null)
        mBalloonText = view.findViewById(R.id.balloon_snippet) as EditText
        mBalloonText.addTextChangedListener(mContentWatcher)

        val dialog = AlertDialog.Builder(context)
                                .setTitle(getString(R.string.dialog_title_balloon))
                                .setView(view)
                                .setPositiveButton(android.R.string.ok, this)
                                .setNegativeButton(android.R.string.cancel, this)
                                .create()

        // ダイアログを表示した際にキーボード表示する。
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        return dialog
    }

    /** {@inheritDoc} */
    override fun onResume() {
        super.onResume()

        val dialog = dialog as AlertDialog
        mPositiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        mPositiveButton.isEnabled = !TextUtils.isEmpty(mBalloonText.text)
    }

    /** {@inheritDoc} */
    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                mHandler.sendMessage {
                    what = MSG_SNIPPET_OK
                    obj  = mBalloonText.text.toString()
                }
            }
            DialogInterface.BUTTON_NEGATIVE -> { mHandler.sendEmptyMessage(MSG_SNIPPET_CANCEL) }
        }
    }

    /**
     * ダイアログを表示するためのユーティリティメソッド
     */
    fun show(fm: FragmentManager) {
        isCancelable = false
        show(fm, TAG_DIALOG)
    }
}