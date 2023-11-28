package com.zhangke.framework.utils

import android.net.Uri
import java.io.InputStream

data class ContentProviderFile(
    val uri: Uri,
    val fileName: String,
    val size: StorageSize,
    val mimeType: String,
    private val inputStreamProvider: () -> InputStream?,
) {

    fun openInputStream(): InputStream? {
        return inputStreamProvider()
    }

    val isVideo: Boolean get() = mimeType.contains("video")
}

