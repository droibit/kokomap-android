package com.droibit.kokomap.fragment.dialog

import android.app.Dialog
import android.os.Bundle
import android.app.DialogFragment
import android.app.FragmentManager
import com.droibit.kokomap.R
import de.psdev.licensesdialog.LicensesDialog

/**
 * オープンソースライセンスを表示するダイアログ
 *
 * @auther kumagai
 * @since 15/06/29
 */
class LicensesDialogFragment: DialogFragment() {

    companion object {
        private val TAG_DIALOG = "licenses"
    }

    /** {@inheritDoc} */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = LicensesDialog.Builder(getActivity())
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