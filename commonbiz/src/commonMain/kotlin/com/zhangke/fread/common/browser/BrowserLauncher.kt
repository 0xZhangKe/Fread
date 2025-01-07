package com.zhangke.fread.common.browser

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.status.model.IdentityRole

interface ActivityBrowserLauncher {

    fun launchBySystemBrowser(url: String) {
        launchBySystemBrowser(url.toPlatformUri())
    }

    fun launchBySystemBrowser(uri: PlatformUri)

    fun launchWebTabInApp(
        url: String,
        role: IdentityRole? = null,
        checkAppSupportPage: Boolean = true,
    ) {
        launchWebTabInApp(
            uri = url.toPlatformUri(),
            role = role,
            checkAppSupportPage = checkAppSupportPage,
        )
    }

    fun launchWebTabInApp(
        uri: PlatformUri,
        role: IdentityRole? = null,
        checkAppSupportPage: Boolean = true,
    )

    fun launchFreadLandingPage() {
        launchWebTabInApp(AppCommonConfig.WEBSITE)
    }

    fun launchAuthorWebsite() {
        launchWebTabInApp(AppCommonConfig.AUTHOR_WEBSITE)
    }
}

val LocalActivityBrowserLauncher = staticCompositionLocalOf<ActivityBrowserLauncher> {
    error("No ActivityBrowserLauncher provided")
}
