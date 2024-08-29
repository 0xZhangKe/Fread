package com.zhangke.framework.blurhash

import android.graphics.Bitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun bitmapFromBuffer(buffer: IntArray, width: Int, height: Int): ImageBitmap {
    return Bitmap.createBitmap(buffer, width, height, Bitmap.Config.ARGB_8888).asImageBitmap()
}
