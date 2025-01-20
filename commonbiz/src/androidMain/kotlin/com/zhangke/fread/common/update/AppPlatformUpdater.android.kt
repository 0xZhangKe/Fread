package com.zhangke.fread.common.update

import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.utils.SystemPageUtils
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.common.config.AppCommonConfig

actual class AppPlatformUpdater {

    actual val platformName: String = "android"

    actual fun getAppVersionCode(): Long {
        return appContext.packageManager
            .getPackageInfo(appContext.packageName, 0)
            .versionCode
            .toLong()
    }

    actual fun triggerUpdate(releaseInfo: AppReleaseInfo) {
        if (SystemPageUtils.openAppMarket(appContext)) return
        val activity = TopActivityManager.topActiveActivity ?: return
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.launchUrl(activity, AppCommonConfig.WEBSITE.toUri())
    }
}
