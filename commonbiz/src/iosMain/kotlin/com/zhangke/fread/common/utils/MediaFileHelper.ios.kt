package com.zhangke.fread.common.utils

import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class MediaFileHelper @Inject constructor() {
    actual suspend fun saveImageToGallery(url: String) {
        TODO("Not yet implemented")
    }
}