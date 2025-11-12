package com.zhangke.fread.common

import com.zhangke.fread.common.browser.AndroidSystemBrowserLauncher
import com.zhangke.fread.common.browser.SystemBrowserLauncher
import me.tatarka.inject.annotations.Provides

actual interface CommonActivityPlatformComponent {
    @Provides
    fun AndroidSystemBrowserLauncher.binds(): SystemBrowserLauncher = this
}
