package com.zhangke.fread.common.browser

import android.content.Context
import android.content.Intent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toAndroidUri
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.status.model.IdentityRole

actual class BrowserLauncher(
    private val context: Context,
) {

    actual fun launchWebTabInApp(
        url: String,
        role: IdentityRole?,
        checkAppSupportPage: Boolean,
    ) {
        launchWebTabInApp(
            uri = url.toPlatformUri(),
            role = role,
            checkAppSupportPage = checkAppSupportPage,
        )
    }

    actual fun launchWebTabInApp(
        uri: PlatformUri,
        role: IdentityRole?,
        checkAppSupportPage: Boolean,
    ) {
        if (role != null && checkAppSupportPage) {
            BrowserBridgeDialogActivity.open(context, role, uri.toString())
            return
        }
        val customTabsIntent = CustomTabsIntent.Builder()
            .build()
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        customTabsIntent.launchUrl(context, uri.toAndroidUri())
    }

    fun launchBySystemBrowser(url: String) {
        launchBySystemBrowser(url.toPlatformUri())
    }

    fun launchBySystemBrowser(uri: PlatformUri) {
        val intent = Intent(Intent.ACTION_VIEW, uri.toAndroidUri())
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun launchFreadLandingPage() {
        launchWebTabInApp(AppCommonConfig.WEBSITE)
    }

    fun launchAuthorWebsite() {
        launchWebTabInApp(AppCommonConfig.AUTHOR_WEBSITE)
    }
}

typealias ActivityBrowserLauncher = BrowserLauncher

