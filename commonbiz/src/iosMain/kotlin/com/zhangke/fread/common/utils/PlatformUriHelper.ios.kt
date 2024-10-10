package com.zhangke.fread.common.utils

import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class PlatformUriHelper @Inject constructor() {
    actual suspend fun read(uri: PlatformUri): ContentProviderFile? {
        TODO("Not yet implemented")
    }

    actual suspend fun readBytes(uri: PlatformUri): ByteArray? {
        TODO("Not yet implemented")
    }

    actual fun queryFileName(uri: PlatformUri): String? {
        TODO("Not yet implemented")
    }
}
