package com.zhangke.framework.media

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import coil.imageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.zhangke.framework.permission.hasWriteStoragePermission
import com.zhangke.framework.utils.ifDebugging
import java.net.URLDecoder

object MediaFileUtil {

    suspend fun saveImageToGallery(context: Context, url: String): Boolean {
        if (!context.hasWriteStoragePermission()) return false
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${System.currentTimeMillis()}.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/Fread")
            }
        }
        val uri = context.contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return false
        val outputStream = context.contentResolver.openOutputStream(uri) ?: return false
        val bitmap = downloadImage(context, url) ?: return false
        outputStream.use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return true
    }

    private suspend fun downloadImage(context: Context, url: String): Bitmap? {
        val request = ImageRequest.Builder(context)
            .data(url)
            .build()
        return try {
            val result = context.imageLoader.execute(request)
            if (result is SuccessResult) {
                (result.drawable as? BitmapDrawable)?.bitmap
            } else {
                null
            }
        } catch (e: Throwable) {
            ifDebugging {
                e.printStackTrace()
            }
            null
        }
    }

    fun queryFileName(context: Context, uri: Uri): String {
        context.contentResolver
            .query(uri, null, null, null, null)
            ?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    if (columnIndex >= 0) {
                        return cursor.getString(columnIndex)
                    }
                }
            }
        return try {
            val path = URLDecoder.decode(uri.path, "UTF-8")
            path.split("/").lastOrNull() ?: path
        } catch (e: Throwable) {
            uri.toString()
        }
    }
}
