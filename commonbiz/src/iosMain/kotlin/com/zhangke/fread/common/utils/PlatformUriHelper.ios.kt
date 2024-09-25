package com.zhangke.fread.common.utils

import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class PlatformUriHelper @Inject constructor() {
    actual suspend fun read(uri: PlatformUri): PlatformUriStream? {
        TODO("Not yet implemented")
    }
}

actual class PlatformUriStream {
    actual val fileName: String
        get() = TODO("Not yet implemented")
    actual val fileSize: Long
        get() = TODO("Not yet implemented")
    actual val mimeType: String
        get() = TODO("Not yet implemented")

    actual suspend fun <R> use(block: suspend (ByteArray?) -> R): R {
        TODO("Not yet implemented")
    }
}