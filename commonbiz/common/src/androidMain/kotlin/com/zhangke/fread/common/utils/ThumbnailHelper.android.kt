package com.zhangke.fread.common.utils

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.getThumbnail
import com.zhangke.framework.utils.toAndroidUri
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class ThumbnailHelper @Inject constructor(
    private val context: ApplicationContext,
) {
    actual fun getThumbnail(uri: PlatformUri): ImageBitmap? {
        return uri.toAndroidUri().getThumbnail(context)?.asImageBitmap()
    }
}
