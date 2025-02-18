package com.zhangke.framework.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

actual class ImageCompressUtils {

    actual fun compress(bytes: ByteArray, targetSize: StorageSize): ByteArray {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return bytes
        if (bytes.size < targetSize.bytes) return bytes
        var quality = 90
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        val step = 3
        while (out.size() > targetSize.bytes && quality > step) {
            out.reset()
            quality -= step
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        return out.toByteArray()
    }
}
