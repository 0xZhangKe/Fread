package com.zhangke.fread.common.utils

import com.zhangke.fread.common.di.ApplicationContext
import okio.Path
import okio.Path.Companion.toOkioPath

actual class StorageHelper (
    private val applicationContext: ApplicationContext,
) {
    actual val cacheDir: Path
        get() = applicationContext.cacheDir.toOkioPath()
}