package com.zhangke.framework.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import java.io.FileNotFoundException

fun Uri.toContentProviderFile(context: Context): ContentProviderFile? {
    val contentResolver = context.contentResolver
    if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
        contentResolver?.queryNameAndSize(this)
            ?.use { cursor ->
                val size = cursor.getSize() ?: StorageSize(0L)
                val name = cursor.getDisplayName().orEmpty()
                return ContentProviderFile(
                    uri = this,
                    fileName = name,
                    size = size,
                    mimeType = contentResolver.getType(this).orEmpty(),
                    inputStreamProvider = {
                        contentResolver.openInputStream(this)
                    }
                )
            }
    }
    return try {
        contentResolver.openAssetFileDescriptor(this, "r")
            ?.use { descriptor ->
                val size = StorageSize(descriptor.length)
                val fileName = getAssetFileNameFromUri(this).orEmpty()
                val extension = getExtensionFromFileName(fileName)
                ContentProviderFile(
                    uri = this,
                    fileName = fileName,
                    size = size,
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                        .orEmpty(),
                    inputStreamProvider = { descriptor.createInputStream() }
                )
            }
    } catch (_: FileNotFoundException) {
        null
    }
}

private const val ASSET_PATH = "android_asset"

private fun getAssetFileNameFromUri(uri: Uri): String? {
    val uriPath = uri.path ?: return null
    if (uri.scheme != "file" || !uriPath.contains(ASSET_PATH)) return null
    return uriPath.substring(uriPath.indexOf(ASSET_PATH) + ASSET_PATH.length + 1)
}

private fun getExtensionFromFileName(fileName: String): String? {
    val array = fileName.split(".")
    if (array.size < 2) return null
    return array.last()
}

private fun ContentResolver.queryNameAndSize(uri: Uri): Cursor? {
    return query(uri, arrayOf(OpenableColumns.SIZE, OpenableColumns.DISPLAY_NAME), null, null, null)
}

private fun Cursor.getSize(): StorageSize? {
    val sizeIndex = getColumnIndex(OpenableColumns.SIZE)
    if (sizeIndex != -1) {
        moveToFirst()
        try {
            return getLongOrNull(sizeIndex)?.let(::StorageSize)
        } catch (_: Throwable) {
            // ignore
        }
    }
    return null
}

private fun Cursor.getDisplayName(): String? {
    val nameIndex = getColumnIndex(OpenableColumns.DISPLAY_NAME)
    if (nameIndex != -1) {
        moveToFirst()
        try {
            return getStringOrNull(nameIndex)
        } catch (_: Throwable) {
            // ignore
        }
    }
    return null
}

fun Uri.getThumbnail(context: Context): Bitmap? {
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    try {
        context.contentResolver
            .query(this, filePathColumn, null, null, null)
            ?.use { cursor ->
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val picturePath = cursor.getString(columnIndex)
                return ThumbnailUtils.createVideoThumbnail(
                    picturePath,
                    MediaStore.Video.Thumbnails.MICRO_KIND
                )
            }
    } catch (_: Throwable) {
    }
    return null
}
