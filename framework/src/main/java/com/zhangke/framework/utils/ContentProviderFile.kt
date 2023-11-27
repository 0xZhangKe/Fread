package com.zhangke.framework.utils

import java.io.InputStream

data class ContentProviderFile(
    val fileName: String,
    val size: StorageSize,
    val mimeType: String,
    private val inputStreamProvider: () -> InputStream?,
) {

    fun openInputStream(): InputStream? {
        return inputStreamProvider()
    }
}
