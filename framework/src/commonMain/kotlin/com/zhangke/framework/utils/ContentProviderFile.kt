package com.zhangke.framework.utils

data class ContentProviderFile(
    val uri: PlatformUri,
    val fileName: String,
    val size: StorageSize,
    val mimeType: String,
    private val streamProvider: () -> ByteArray?,
) {
    fun readBytes(): ByteArray? {
        return streamProvider()
    }

    val isVideo: Boolean get() = mimeType.contains("video")
}

