package com.droibit.kokomap.model

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.*
import android.provider.MediaStore.MediaColumns
import java.io.File

/**
 * 保存したjpeg画像をMediaStoreに登録する。
 *
 * Created by kumagai on 2015/07/02.
 *
 * @constructor 新しいインスタンスを作成します。
 * @param context コンテキスト
 */
class MediaRegistrant constructor(context: Context) {

    companion object {
        val MIME_JPEG = "image/jpeg"
    }

    private val mContext = context

    /**
     * 対象の画像ファイルをMediaStoreに登録します。
     *
     * @param filePath 画像ファイルのパス
     * @return 登録後の画像ファイルのURI（content://...)
     */
    public fun register(filePath: String): Uri? {
        return mContext.getContentResolver().insertExternalContent {
            val imgFile = File(filePath)
            put(MediaColumns.TITLE, imgFile.name)
            put(MediaColumns.DISPLAY_NAME, imgFile.name)
            put(MediaColumns.MIME_TYPE, MIME_JPEG)
            put(MediaColumns.DATA, imgFile.getAbsolutePath())
            put(ImageColumns.DATE_TAKEN, imgFile.lastModified())
        }
    }
}

/**
 * 内部ストレージに保存した画像をMediaStoreに登録します。
 */
private fun ContentResolver.insertExternalContent(init: ContentValues.()->Unit): Uri {
    val values = ContentValues()
    values.init()
    return insert(Media.EXTERNAL_CONTENT_URI, values)
}
