package com.droibit.kokomap.extension

import android.support.design.widget.Snackbar
import android.view.View

/**
 * Support Design ライブラリの拡張を定義する
 *
 * Created by kumagai on 2015/06/26.
 */


/**
 * スナックバーを表示する
 */
fun showSnackbar(view: View, resId: Int, duration: Int) = Snackbar.make(view, resId, duration).show()