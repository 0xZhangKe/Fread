package com.zhangke.fread.common.browser

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.fread.status.model.IdentityRole

expect class BrowserLauncher {

    fun launchWebTabInApp(
        url: String,
        role: IdentityRole? = null,
        checkAppSupportPage: Boolean = true,
    )

    fun launchWebTabInApp(
        uri: PlatformUri,
        role: IdentityRole? = null,
        checkAppSupportPage: Boolean = true,
    )

    fun launchBySystemBrowser(url: String)

    fun launchBySystemBrowser(uri: PlatformUri)

    fun launchFreadLandingPage()

    fun launchAuthorWebsite()
}

val LocalActivityBrowserLauncher = staticCompositionLocalOf<BrowserLauncher> {
    error("No ActivityBrowserLauncher provided")
}
