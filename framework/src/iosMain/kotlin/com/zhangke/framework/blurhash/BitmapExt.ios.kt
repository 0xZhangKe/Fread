package com.zhangke.framework.blurhash

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.ColorAlphaType
import org.jetbrains.skia.ImageInfo

actual fun bitmapFromBuffer(buffer: IntArray, width: Int, height: Int): ImageBitmap {
    val byteArray = getBytes(width, height, buffer)
    return Bitmap().apply {
        allocPixels(ImageInfo.makeS32(width, height, ColorAlphaType.PREMUL))
        installPixels(imageInfo, byteArray, width * 4)
    }.asComposeImageBitmap()
}

private fun getBytes(width: Int, height: Int, buffer: IntArray): ByteArray {
    val pixels = ByteArray(width * height * 4)

    var index = 0
    for (y in 0 until height) {
        for (x in 0 until width) {
            val pixel = buffer[y * width + x]
            pixels[index++] = ((pixel and 0xFF)).toByte() // Blue component
            pixels[index++] = (((pixel shr 8) and 0xFF)).toByte() // Green component
            pixels[index++] = (((pixel shr 16) and 0xFF)).toByte() // Red component
            pixels[index++] = (((pixel shr 24) and 0xFF)).toByte() // Alpha component
        }
    }

    return pixels
}