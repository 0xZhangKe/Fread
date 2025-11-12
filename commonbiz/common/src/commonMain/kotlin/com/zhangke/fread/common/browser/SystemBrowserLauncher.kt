package com.zhangke.fread.common.browser

import com.zhangke.framework.utils.PlatformUri

interface SystemBrowserLauncher {

    fun launchBySystemBrowser(uri: PlatformUri)

    fun launchWebTabInApp(uri: PlatformUri)
}
