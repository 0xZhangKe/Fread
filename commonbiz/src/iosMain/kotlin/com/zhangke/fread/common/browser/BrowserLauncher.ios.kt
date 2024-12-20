package com.zhangke.fread.common.browser

import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.common.config.AppCommonConfig
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

@ApplicationScope
actual class BrowserLauncher @Inject constructor() {
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
        TODO("Not yet implemented")
    }

    actual fun launchBySystemBrowser(url: String) {
        launchBySystemBrowser(url.toPlatformUri())
    }

    actual fun launchBySystemBrowser(uri: PlatformUri) {
        TODO("Not yet implemented")
    }

    actual fun launchFreadLandingPage() {
        launchWebTabInApp(AppCommonConfig.WEBSITE)
    }

    actual fun launchAuthorWebsite() {
        launchWebTabInApp(AppCommonConfig.AUTHOR_WEBSITE)
    }
}

typealias ActivityBrowserLauncher = BrowserLauncher
