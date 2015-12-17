package com.droibit.kokomap.model

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore.Images.ImageColumns
import android.provider.MediaStore.Images.Media
import android.provider.MediaStore.MediaColumns
import java.io.File

/**
 * 保存したjpeg画像をMediaStoreに登録する。
 *
 * @author kumagai
 *
 * @constructor 新しいインスタンスを作成します。
 * @param context コンテキスト
 */
class MediaRegistrant constructor(context: Context) {

    companion object {
        const val MIME_JPEG = "image/jpeg"
    }

    private val mContext = context

    /**
     * 対象の画像ファイルをMediaStoreに登録します。
     *
     * @param filePath 画像ファイルのパス
     * @return 登録後の画像ファイルのURI（content://...)
     */
    public fun register(filePath: String): Uri? {
        val values = ContentValues().apply {
            val imgFile = File(filePath)
            put(MediaColumns.TITLE, imgFile.name)
            put(MediaColumns.DISPLAY_NAME, imgFile.name)
            put(MediaColumns.MIME_TYPE, MIME_JPEG)
            put(MediaColumns.DATA, imgFile.absolutePath)
            put(ImageColumns.DATE_TAKEN, imgFile.lastModified())
        }
        return mContext.contentResolver.insert(Media.EXTERNAL_CONTENT_URI, values)
    }
}