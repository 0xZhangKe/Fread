package com.zhangke.framework.media

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.seiko.imageloader.imageLoader
import com.seiko.imageloader.model.ImageRequest
import com.seiko.imageloader.model.ImageResult
import com.zhangke.framework.architect.http.sharedHttpClient
import com.zhangke.framework.imageloader.executeSafety
import com.zhangke.framework.permission.hasWriteStoragePermission
import com.zhangke.framework.utils.ifDebugging
import com.zhangke.framework.utils.throwInDebug
import io.ktor.client.request.prepareRequest
import io.ktor.client.statement.bodyAsChannel
import io.ktor.utils.io.jvm.javaio.copyTo
import java.io.OutputStream
import java.net.URLDecoder

object MediaFileUtil {

    suspend fun saveImageToGallery(context: Context, url: String): Boolean {
        if (!context.hasWriteStoragePermission()) return false
        val contentValues = buildContentValues(
            fileName = "${System.currentTimeMillis()}.jpg",
            mediaType = "image/jpeg",
            directory = Environment.DIRECTORY_PICTURES,
        )
        val uri = context.contentResolver
            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues) ?: return false
        val outputStream = context.contentResolver.openOutputStream(uri)
        if (outputStream == null) {
            context.contentResolver.delete(uri, null, null)
            return false
        }
        val bitmap = downloadImage(context, url)
        if (bitmap == null) {
            context.contentResolver.delete(uri, null, null)
            return false
        }
        outputStream.use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        }
        return true
    }

    suspend fun saveVideoToGallery(context: Context, url: String): Boolean {
        if (!context.hasWriteStoragePermission()) return false
        try {
            downloadVideo(url) { contentType ->
                val uri = context.insertVideoMedia(contentType) ?: return@downloadVideo null
                val outputStream =
                    context.contentResolver.openOutputStream(uri) ?: return@downloadVideo null
                outputStream
            }
        } catch (e: Throwable) {
            throwInDebug("saveVideoToGallery", e)
            return false
        }
        return true
    }

    private fun Context.insertVideoMedia(mediaType: String): Uri? {
        val fileExtension = mediaType.split("/").lastOrNull() ?: "mp4"
        val fileName = "${System.currentTimeMillis()}.$fileExtension"
        val contentValues = buildContentValues(fileName, mediaType, Environment.DIRECTORY_MOVIES)
        return contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    private fun buildContentValues(
        fileName: String,
        mediaType: String,
        directory: String,
    ): ContentValues {
        return ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, mediaType)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "$directory/Fread")
            }
        }
    }

    private suspend fun downloadImage(context: Context, url: String): Bitmap? {
        return try {
            val request = ImageRequest(url) {
                options {
                    isBitmap = true
                }
            }
            when (val result = context.imageLoader.executeSafety(request)) {
                is ImageResult.OfBitmap -> result.bitmap
                is ImageResult.OfImage -> result.image.drawable.let { it as? BitmapDrawable }?.bitmap
                else -> null
            }
        } catch (e: Throwable) {
            ifDebugging {
                e.printStackTrace()
            }
            null
        }
    }

    private suspend inline fun downloadVideo(
        url: String,
        crossinline block: (String) -> OutputStream?
    ) {
        sharedHttpClient.prepareRequest(url).execute { response ->
            val contentType = response.headers["Content-Type"] ?: "video/mp4"
            val outputStream = block(contentType) ?: return@execute
            response.bodyAsChannel().copyTo(outputStream)
            outputStream.close()
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
