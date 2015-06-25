package com.droibit.kokomap.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import com.droibit.kokomap.R

/**
 * 地図が象を保存中に表示するプログレスダイアログ
 *
 * Created by kumagai on 2015/06/25.
 */
class ProgressDialogFragment: DialogFragment() {

    companion object {
        val TAG_DIALOG = "progress_dialog"
    }

    /** {@inheritDoc} */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog? {
        return AlertDialog.Builder(getActivity())
                          .setView(R.layout.dialog_progress)
                          .create()
    }

    /**
     * ダイアログを表示するためのユーティリティメソッド
     */
    fun show(fm: FragmentManager) {
        setCancelable(false)
        show(fm, TAG_DIALOG)
    }
}
