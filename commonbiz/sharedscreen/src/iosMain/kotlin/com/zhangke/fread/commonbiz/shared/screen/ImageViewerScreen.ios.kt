package com.zhangke.fread.commonbiz.shared.screen

import com.seiko.imageloader.model.ImageResult

internal actual fun ImageResult.aspectRatio(): Float? {
    return when (this) {
        is ImageResult.OfBitmap -> bitmap.width.toFloat() / bitmap.height
        is ImageResult.OfImage -> image.width.toFloat() / image.height
        else -> null
    }
}