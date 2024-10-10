package com.zhangke.fread.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.ImageBitmap
import com.zhangke.framework.utils.PlatformUri

expect class ThumbnailHelper {
    fun getThumbnail(uri: PlatformUri): ImageBitmap?
}

val LocalThumbnailHelper = staticCompositionLocalOf<ThumbnailHelper> {
    error("No ThumbnailHelper provided")
}
