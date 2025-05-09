package com.zhangke.fread.common.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.framework.utils.ContentProviderFile
import com.zhangke.framework.utils.PlatformUri

expect class PlatformUriHelper {

    suspend fun read(uri: PlatformUri): ContentProviderFile?

    suspend fun readBytes(uri: PlatformUri): ByteArray?

    fun queryFileName(uri: PlatformUri): String?
}

val LocalPlatformUriHelper = staticCompositionLocalOf<PlatformUriHelper> {
    error("No PlatformUriHelper provided")
}