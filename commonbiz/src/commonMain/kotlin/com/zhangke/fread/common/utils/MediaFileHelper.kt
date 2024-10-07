package com.zhangke.fread.common.utils

import androidx.compose.runtime.staticCompositionLocalOf

expect class MediaFileHelper {
    fun saveImageToGallery(url: String)
    fun saveVideoToGallery(url: String)
}

val LocalMediaFileHelper = staticCompositionLocalOf<MediaFileHelper> { error("No MediaFileHelper provided") }
