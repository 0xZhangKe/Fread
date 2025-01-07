package com.zhangke.fread.common

import com.zhangke.fread.common.browser.ActivityBrowserLauncher
import com.zhangke.fread.common.browser.AndroidActivityBrowserLauncher
import me.tatarka.inject.annotations.Provides

actual interface CommonActivityPlatformComponent {
    @Provides
    fun AndroidActivityBrowserLauncher.binds(): ActivityBrowserLauncher = this
}