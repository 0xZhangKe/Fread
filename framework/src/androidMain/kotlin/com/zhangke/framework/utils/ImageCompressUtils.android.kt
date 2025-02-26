package com.zhangke.framework.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

actual class ImageCompressUtils {

    actual fun compress(bytes: ByteArray, targetSize: StorageSize): CompressResult {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            ?: return CompressResult(bytes, null)
        if (bytes.size < targetSize.bytes) {
            return CompressResult(bytes, bitmap.aspectRatio)
        }
        var quality = 90
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        val step = 3
        while (out.size() > targetSize.bytes && quality > step) {
            out.reset()
            quality -= step
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        runCatching { bitmap.recycle() }
        val resultBytes = out.toByteArray()
        val tmpBitmap = BitmapFactory.decodeByteArray(resultBytes, 0, resultBytes.size)
        val resultSize = tmpBitmap?.aspectRatio
        runCatching { tmpBitmap?.recycle() }
        return CompressResult(resultBytes, resultSize)
    }

    private val Bitmap.aspectRatio: AspectRatio
        get() = AspectRatio(width = width.toLong(), height = height.toLong())
}
