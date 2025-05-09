package com.zhangke.fread.common.utils

import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject
import okio.Path
import okio.Path.Companion.toOkioPath

@ApplicationScope
actual class StorageHelper @Inject constructor(
    private val applicationContext: ApplicationContext,
) {
    actual val cacheDir: Path
        get() = applicationContext.cacheDir.toOkioPath()
}