package com.zhangke.fread.common.utils

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
    actual suspend fun read(uri: PlatformUri): PlatformUriStream? {
        val contentFile = withContext(Dispatchers.IO) {
            uri.toAndroidUri().toContentProviderFile(context)
        }
        return contentFile?.let { PlatformUriStream(it) }
    }
}

actual class PlatformUriStream internal constructor(
    private val contentFile: ContentProviderFile,
) {
    actual val fileName: String
        get() = contentFile.fileName
    actual val fileSize: Long
        get() = contentFile.size.length
    actual val mimeType: String
        get() = contentFile.mimeType

    actual suspend fun <R> use(block: suspend (ByteArray?) -> R): R {
        return contentFile.openInputStream().use {
            block(it?.readBytes())
        }
    }
}