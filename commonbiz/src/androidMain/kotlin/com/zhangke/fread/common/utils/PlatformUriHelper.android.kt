package com.zhangke.fread.common.utils

import com.zhangke.framework.media.MediaFileUtil
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toAndroidUri
import com.zhangke.framework.utils.toContentProviderFile
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class PlatformUriHelper @Inject constructor(
    private val context: ApplicationContext,
) {
    actual suspend fun read(uri: PlatformUri): ContentProviderFile? {
        val contentFile = withContext(Dispatchers.IO) {
            uri.toAndroidUri().toContentProviderFile(context)
        }
        return contentFile
    }

    actual suspend fun readBytes(uri: PlatformUri): ByteArray? {
        return context.contentResolver.openInputStream(uri.toAndroidUri())?.use {
            it.readBytes()
        }
    }

    actual fun queryFileName(uri: PlatformUri): String? {
        return MediaFileUtil.queryFileName(context, uri.toAndroidUri())
    }
}
