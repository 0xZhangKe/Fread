package com.zhangke.fread.common.alttext

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

actual fun resizeAndJpegBase64(
    bytes: ByteArray,
    maxLongestSide: Int,
    quality: Int,
): String {
    val source = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        ?: error("Image decode failed")
    val maxDim = maxOf(source.width, source.height)
    val scaled = if (maxDim > maxLongestSide) {
        val scale = maxLongestSide.toFloat() / maxDim
        val w = (source.width * scale).toInt().coerceAtLeast(1)
        val h = (source.height * scale).toInt().coerceAtLeast(1)
        Bitmap.createScaledBitmap(source, w, h, true).also {
            if (it !== source) runCatching { source.recycle() }
        }
    } else {
        source
    }
    val out = ByteArrayOutputStream()
    scaled.compress(Bitmap.CompressFormat.JPEG, quality, out)
    runCatching { scaled.recycle() }
    return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
}
