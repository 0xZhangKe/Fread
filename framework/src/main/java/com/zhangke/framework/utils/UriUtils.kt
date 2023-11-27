package com.zhangke.framework.utils

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import java.io.FileNotFoundException

fun Uri.toContentProviderFile(): ContentProviderFile? {
    val contentResolver = appContext.contentResolver
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
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension).orEmpty(),
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

fun uriString(
    scheme: String,
    host: String,
    path: String,
    queries: Map<String, String>,
): String {
    val builder = StringBuilder()
    if (scheme.isNotEmpty()) {
        builder.append(scheme)
        builder.append("://")
    }
    builder.append(host)
    var fixedPath = path
    if (builder.endsWith("/") && path.startsWith("/")) {
        fixedPath = fixedPath.removePrefix("/")
    }
    if (fixedPath.endsWith("/") && queries.isNotEmpty()) {
        fixedPath = fixedPath.removeSuffix("/")
    }
    builder.append(fixedPath)
    if (queries.isNotEmpty()) {
        val query = queries.entries
            .joinToString(prefix = "?", separator = "&") { "${it.key}=${Uri.encode(it.value)}" }
        builder.append(query)
    }
    return builder.toString()
}
