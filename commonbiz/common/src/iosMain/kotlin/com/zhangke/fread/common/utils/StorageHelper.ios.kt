package com.zhangke.fread.common.utils

import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Inject
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

@ApplicationScope
actual class StorageHelper @Inject constructor() {
    actual val cacheDir: Path
        get() = getCacheDir().toPath()
}

private fun getCacheDir(): String {
    return NSSearchPathForDirectoriesInDomains(
        NSCachesDirectory,
        NSUserDomainMask,
        true,
    ).first() as String
}