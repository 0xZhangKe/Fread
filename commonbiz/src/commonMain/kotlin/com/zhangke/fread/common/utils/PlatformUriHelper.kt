package com.zhangke.fread.common.utils

import com.zhangke.framework.utils.PlatformUri

expect class PlatformUriHelper {

    suspend fun read(uri: PlatformUri): PlatformUriStream?
}

expect class PlatformUriStream {
    val fileName: String
    val fileSize: Long
    val mimeType: String

    suspend fun <R> use(block: suspend (ByteArray?) -> R): R
}