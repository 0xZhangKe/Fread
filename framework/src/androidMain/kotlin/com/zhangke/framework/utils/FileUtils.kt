package com.zhangke.framework.utils

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import java.io.FileNotFoundException

object FileUtils {

    /**
     * @return unit is KB
     */
    fun getFileSizeByUri(uri: Uri): StorageSize? {
        val contentResolver = appContext.contentResolver
        var bytes: Long? = null
        if (uri.scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            contentResolver.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
                ?.use { cursor ->
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex != -1) {
                        cursor.moveToFirst()
                        try {
                            bytes = cursor.getLong(sizeIndex)
                        } catch (_: Throwable) {
                            // ignore
                        }
                    }
                }
        }
        if (bytes == null) {
            try {
                contentResolver.openAssetFileDescriptor(uri, "r")
                    ?.use { bytes = it.length }
            } catch (_: FileNotFoundException) {
                // ignore
            }
        }
        return bytes?.let(::StorageSize)
    }
}