package com.droibit.kokomap.fragment.dialog

import android.app.Dialog
import android.app.DialogFragment
import android.app.FragmentManager
import android.os.Bundle
import com.droibit.kokomap.R
import de.psdev.licensesdialog.LicensesDialog

/**
 * オープンソースライセンスを表示するダイアログ
 *
 * @author kumagai
 */
class LicensesDialogFragment: DialogFragment() {

    companion object {
        const private val TAG_DIALOG = "licenses"
    }

    /** {@inheritDoc} */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = LicensesDialog.Builder(context)
                                   .setNotices(R.raw.licenses)
                                   .setShowFullLicenseText(true)
                                   .build()
                                   .createAppCompat();
        dialog.setOnDismissListener(null);
        return dialog
    }

    fun show(fm: FragmentManager) {
        show(fm, TAG_DIALOG)
    }
}