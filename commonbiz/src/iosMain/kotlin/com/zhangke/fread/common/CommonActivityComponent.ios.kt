package com.zhangke.fread.common

import com.zhangke.fread.common.browser.ActivityBrowserLauncher
import com.zhangke.fread.common.browser.IosActivityBrowserLauncher
import me.tatarka.inject.annotations.Provides

actual interface CommonActivityPlatformComponent {
    @Provides
    fun IosActivityBrowserLauncher.binds(): ActivityBrowserLauncher = this
}
