package com.zhangke.fread.common

import com.zhangke.fread.common.browser.IosSystemBrowserLauncher
import com.zhangke.fread.common.browser.SystemBrowserLauncher
import me.tatarka.inject.annotations.Provides

actual interface CommonActivityPlatformComponent {

    @Provides
    fun IosSystemBrowserLauncher.binds(): SystemBrowserLauncher = this
}
