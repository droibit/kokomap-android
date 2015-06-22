package com.droibit.kokomap.model

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import com.droibit.kokomap.R
import java.io.File

/**
 * [Bitmap] を内部ストレージに保存するためのクラス。
 * 保存する際の画像形式はpng
 *
 * @auther kumagai
 * @since 15/06/22
 */
public class BitmapWriter {

    companion object {

        /**
         * 画像ファイルの保存先ディレクトリを取得する
         */
        public fun geDestinationDir(context: Context): String {
            val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File(picDir, context.getString(R.string.dest_dir)).getAbsolutePath()
        }
    }
}
