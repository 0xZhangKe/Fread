package com.zhangke.fread.common.update

import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import com.zhangke.framework.activity.TopActivityManager
import com.zhangke.framework.utils.SystemPageUtils
import com.zhangke.framework.utils.appContext
import com.zhangke.framework.utils.getApkSignatureSha1
import com.zhangke.fread.common.config.AppCommonConfig

actual class AppPlatformUpdater {

    companion object {

        private const val F_DROID_SIGN_PUB_KEY =
            "B9:7E:73:42:BD:41:63:A5:D7:D7:1C:5E:35:DF:DF:D1:A9:13:8C:E8"
    }

    actual val platformName: String = "android"

    actual val signingForFDroid: Boolean
        get() {
            val apkSignatureSha1 = appContext.getApkSignatureSha1()
            return apkSignatureSha1 == F_DROID_SIGN_PUB_KEY
            return true
        }

    actual fun getAppVersionCode(): Long {
        return appContext.packageManager
            .getPackageInfo(appContext.packageName, 0)
            .versionCode
            .toLong()
    }

    actual fun triggerUpdate(releaseInfo: AppReleaseInfo) {
        val builtForFDroid = signingForFDroid
        if (builtForFDroid) {
            if (!SystemPageUtils.openSystemViewPage(appContext, AppCommonConfig.F_DROID_URI)) {
                Toast.makeText(appContext, "F-Droid not found", Toast.LENGTH_SHORT).show()
            }
            return
        }
        if (SystemPageUtils.openAppMarket(appContext)) return
        val activity = TopActivityManager.topActiveActivity ?: return
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.launchUrl(activity, AppCommonConfig.WEBSITE.toUri())
    }
}
