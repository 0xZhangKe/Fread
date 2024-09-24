package com.zhangke.fread.common.browser

import com.zhangke.framework.utils.PlatformUri
import com.zhangke.framework.utils.toPlatformUri
import com.zhangke.fread.status.model.IdentityRole

actual class BrowserLauncher {
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
}