package com.zhangke.fread.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher

typealias IosFreadApp = @Composable () -> Unit

@Composable
internal fun IosFreadApp(
    activityBrowserLauncher: BrowserLauncher,
    freadApp: FreadApp,
) {
    CompositionLocalProvider(
        LocalActivityBrowserLauncher provides activityBrowserLauncher,
    ) {
        freadApp()
    }
}
