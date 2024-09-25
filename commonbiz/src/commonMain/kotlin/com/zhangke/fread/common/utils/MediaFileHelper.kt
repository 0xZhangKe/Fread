package com.zhangke.fread.common.utils

import androidx.compose.runtime.staticCompositionLocalOf

expect class MediaFileHelper {
    suspend fun saveImageToGallery(url: String)
}

val LocalMediaFileHelper = staticCompositionLocalOf<MediaFileHelper> { error("No MediaFileHelper provided") }
