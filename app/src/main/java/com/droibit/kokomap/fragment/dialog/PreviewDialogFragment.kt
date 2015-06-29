package com.droibit.kokomap.fragment.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ImageView
import com.droibit.easycreator.compat.fragment
import com.droibit.easycreator.sendMessage
import com.droibit.kokomap.R
import com.droibit.kokomap.model.MSG_USER_COMPLETE
import com.droibit.kokomap.model.MSG_USER_RETAKE
import kotlin.properties.Delegates

/**
 * GoogleMapsのスナップショットのプレビューを表示するフラグメント
 *
 * @auther kumagai
 * @since 15/06/25
 */
class PreviewDialogFragment: DialogFragment(), DialogInterface.OnClickListener {

    companion object {
        val TAG_DIALOG = "preview_dialog"

        private val ARG_SNAPSHOT = "snapshot"
        private val ARG_LAUNCHED_PICK_MODE = "pick_mode"

        /**
         * 新しいインスタンスを作成する
         *
         * @param snapshot 表示するビットマップ
         * @param launchedPickMode 他アプリから起動しているか
         */
        fun newInstance(snapshot: Bitmap, launchedPickMode: Boolean): PreviewDialogFragment = fragment { args ->
            args.putParcelable(ARG_SNAPSHOT, snapshot)
            args.putBoolean(ARG_LAUNCHED_PICK_MODE, launchedPickMode)
        }
    }

    private var mHandler: Handler by Delegates.notNull()

    /** {@inheritDoc} */
    override fun onAttach(activity: Activity?) {
        super<DialogFragment>.onAttach(activity)

        if (activity !is Handler.Callback) {
            throw IllegalStateException("Activity must implements Handler.Callback")
        }
        mHandler = Handler(Looper.getMainLooper(), activity)
    }

    /** {@inheritDoc} */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(getActivity(), R.layout.dialog_preview, null)
        val imageView = view.findViewById(R.id.image) as ImageView
        imageView.setImageBitmap(getArguments().getParcelable(ARG_SNAPSHOT))

        val positiveRes = if (getArguments().getBoolean(ARG_LAUNCHED_PICK_MODE, false))
                            R.string.text_done
                          else
                            R.string.text_save

        return AlertDialog.Builder(getActivity())
                          .setView(view)
                          .setPositiveButton(positiveRes, this)
                          .setNegativeButton(R.string.text_retake, this)
                          .create()
    }

    /** {@inheritDoc} */
    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                mHandler.sendMessage {
                    what = MSG_USER_COMPLETE
                    obj  = getArguments().getParcelable(ARG_SNAPSHOT)
                }
            }
            DialogInterface.BUTTON_NEGATIVE -> {
                mHandler.sendMessage {
                    what = MSG_USER_RETAKE
                    obj  = getArguments().getParcelable(ARG_SNAPSHOT)
                }
            }
        }
    }

    /**
     * ダイアログを表示するためのユーティリティメソッド
     */
    fun show(fm: FragmentManager) {
        setCancelable(false)
        show(fm, TAG_DIALOG)
    }
}