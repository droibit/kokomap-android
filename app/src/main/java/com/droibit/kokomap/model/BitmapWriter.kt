package com.droibit.kokomap.model

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.support.annotation.VisibleForTesting
import android.support.annotation.WorkerThread
import com.droibit.kokomap.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * [Bitmap] を内部ストレージに保存するためのクラス。
 *
 * @auther kumagai
 * @since 15/06/22
 */
public class BitmapWriter constructor(context: Context) {

    companion object {
        val sDateFormatter = SimpleDateFormat("yyMMddHHmmss")

        /**
         * 画像ファイルの保存先ディレクトリを取得する
         */
        fun getDestinationDir(context: Context): File {
            val picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File(picDir, context.getString(R.string.dest_dir))
        }
    }

    private val mContext = context

    /**
     * ビットマップをpng形式の画像ファイルに保存する。
     *
     * @param context コンテキスト
     * @param bitmap 保存対象のビットマップ
     * @return 保存したファイルのパス（コンテンツプロバイダに登録する場合は使用可能）
     */
    @WorkerThread
    public fun write(bitmap: Bitmap): String {
        var destDir = getDestinationDir(mContext)
        if (!destDir.exists() && !destDir.mkdir()) {
            throw IOException("保存先のフォルダの作成できません")
        }

        val destFile = File(destDir, makeFilename(System.currentTimeMillis()))
        FileOutputStream(destFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
        bitmap.recycle()

        return destFile.getAbsolutePath()
    }

    /**
     * 現在の時刻に基づいたファイル名を作成する。
     * 保存する際のファイル名は **kokomap_yyMMddhhmmss.jpg**
     */
    @VisibleForTesting
    fun makeFilename(currentTimeMillis: Long): String {
        val date = Date(currentTimeMillis)
        return mContext.getString(R.string.dest_file_name, sDateFormatter.format(date))
    }
}