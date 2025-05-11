package com.zhangke.fread.common.update

import com.zhangke.fread.common.utils.SystemUtils
import platform.Foundation.NSBundle


actual class AppPlatformUpdater {

    actual val platformName: String = "ios"

    actual val signingForFDroid: Boolean = false

    actual fun getAppVersionCode(): Long {
        val versionCode =
            NSBundle.mainBundle().infoDictionary()?.get("CFBundleVersion") as? String ?: ""
        return versionCode.toLongOrNull() ?: 0L
    }

    actual fun triggerUpdate(releaseInfo: AppReleaseInfo) {
        SystemUtils.openAppStore(NSBundle.mainBundle().bundleIdentifier().orEmpty())
    }
}
