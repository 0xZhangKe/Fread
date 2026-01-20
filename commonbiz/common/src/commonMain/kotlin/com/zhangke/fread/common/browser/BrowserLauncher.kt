package com.zhangke.fread.common.browser

import androidx.compose.runtime.staticCompositionLocalOf
import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.common.utils.GlobalScreenNavigation
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BrowserLauncher (private val systemBrowserLauncher: SystemBrowserLauncher) {

    fun launchBySystemBrowser(url: String) {
        launchBySystemBrowser(url.toPlatformUri())
    }

    fun launchBySystemBrowser(uri: PlatformUri) {
        systemBrowserLauncher.launchBySystemBrowser(uri)
    }

    suspend fun launchWebTabInApp(
        url: String,
        locator: PlatformLocator? = null,
        checkAppSupportPage: Boolean = true,
        isFromExternal: Boolean = false,
    ) {
        launchWebTabInApp(
            uri = url.toPlatformUri(),
            locator = locator,
            checkAppSupportPage = checkAppSupportPage,
            isFromExternal = isFromExternal,
        )
    }

    suspend fun launchWebTabInApp(
        uri: PlatformUri,
        locator: PlatformLocator? = null,
        checkAppSupportPage: Boolean = true,
        isFromExternal: Boolean = false,
    ) {
        if (checkAppSupportPage) {
            GlobalScreenNavigation.navigate(
                screen = UrlRedirectScreenKey(
                    uri = uri.toString(),
                    locator = locator,
                    isFromExternal = isFromExternal,
                ),
            )
        } else {
            systemBrowserLauncher.launchWebTabInApp(uri)
        }
    }

    suspend fun launchFreadLandingPage() {
        launchWebTabInApp(AppCommonConfig.WEBSITE)
    }

    suspend fun launchAuthorWebsite() {
        launchWebTabInApp(AppCommonConfig.AUTHOR_WEBSITE)
    }
}

val LocalActivityBrowserLauncher = staticCompositionLocalOf<BrowserLauncher> {
    error("No ActivityBrowserLauncher provided")
}

fun BrowserLauncher.launchWebTabInApp(
    scope: CoroutineScope,
    url: PlatformUri,
    locator: PlatformLocator? = null,
    checkAppSupportPage: Boolean = true,
) {
    scope.launch { this@launchWebTabInApp.launchWebTabInApp(url, locator, checkAppSupportPage) }
}

fun BrowserLauncher.launchWebTabInApp(
    scope: CoroutineScope,
    url: String,
    locator: PlatformLocator? = null,
    checkAppSupportPage: Boolean = true,
) {
    scope.launch { this@launchWebTabInApp.launchWebTabInApp(url, locator, checkAppSupportPage) }
}