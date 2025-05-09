package com.zhangke.fread.common.utils

import androidx.compose.ui.graphics.ImageBitmap
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class ThumbnailHelper @Inject constructor() {
    actual fun getThumbnail(uri: PlatformUri): ImageBitmap? {
        TODO("Not yet implemented")
    }
}
