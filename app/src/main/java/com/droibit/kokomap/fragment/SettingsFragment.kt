package com.droibit.kokomap.fragment

import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceFragment
import android.support.annotation.StringRes
import android.view.View
import com.droibit.kokomap.BuildConfig
import com.droibit.kokomap.R
import com.droibit.kokomap.fragment.dialog.LicensesDialogFragment

/**
 * アプリの設定画面を表示するためのフラグメント
 *
 * @author kumagai
 */
public class SettingsFragment: PreferenceFragment() {

    /** {@inheritDoc} */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.settings)
    }

    /** {@inheritDoc} */
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefDestDir = findPreference(R.string.pref_app_dest_folder_key)
        prefDestDir.summary = "/sdcard/${Environment.DIRECTORY_PICTURES}/${getString(R.string.dest_dir)}"

        val prefVersion = findPreference(R.string.pref_summary_version_key)
        prefVersion.summary = getString(R.string.pref_summary_version_summary, BuildConfig.VERSION_NAME)

        findPreference(R.string.pref_summary_oss_key).setOnPreferenceClickListener {
            showLicensesDialog()
        }
    }

    private fun findPreference(@StringRes resId: Int) = findPreference(getString(resId))

    private fun showLicensesDialog(): Boolean {
        LicensesDialogFragment().show(fragmentManager)
        return true
    }
}
