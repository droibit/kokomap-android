package com.droibit.kokomap.fragment

import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceFragment
import android.support.annotation.StringRes
import android.view.View
import com.droibit.kokomap.BuildConfig
import com.droibit.kokomap.R
import com.droibit.kokomap.model.BitmapWriter

/**
 * アプリの設定画面を表示するためのフラグメント
 *
 * @auther kumagai
 * @since 15/06/22
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
        prefDestDir.setSummary("/${Environment.DIRECTORY_PICTURES}/${getString(R.string.dest_dir)}")

        val prefVersion = findPreference(R.string.pref_summary_version_key)
        prefVersion.setSummary(getString(R.string.pref_summary_version_summary, BuildConfig.VERSION_NAME))
    }

    private fun findPreference(@StringRes resId: Int) = findPreference(getString(resId))
}
