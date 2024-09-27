package com.zhangke.fread.common

import com.zhangke.fread.common.browser.ActivityBrowserLauncher

actual interface CommonActivityPlatformComponent {
    val activityBrowserLauncher: ActivityBrowserLauncher
}
