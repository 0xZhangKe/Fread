package com.zhangke.framework.blurhash

import androidx.compose.ui.graphics.ImageBitmap

expect fun bitmapFromBuffer(buffer: IntArray, width: Int, height: Int): ImageBitmap
