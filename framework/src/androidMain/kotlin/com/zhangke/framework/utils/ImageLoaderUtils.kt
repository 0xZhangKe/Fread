package com.zhangke.framework.utils

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import com.seiko.imageloader.model.ImageResult

fun ImageResult.asBitmapOrNull(): Bitmap? = when (this) {
    is ImageResult.OfBitmap -> bitmap
    is ImageResult.OfImage -> image.drawable.let { it as? BitmapDrawable }?.bitmap
    else -> null
}
