package com.zhangke.fread.commonbiz.shared.screen

import com.seiko.imageloader.model.ImageResult
import com.zhangke.framework.utils.aspectRatio

internal actual fun ImageResult.aspectRatio(): Float? {
    return when (this) {
        is ImageResult.OfBitmap -> bitmap.width.toFloat() / bitmap.height
        is ImageResult.OfImage -> image.drawable.aspectRatio()
        else -> null
    }
}