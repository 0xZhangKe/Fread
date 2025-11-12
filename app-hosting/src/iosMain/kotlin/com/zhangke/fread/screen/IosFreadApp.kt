package com.zhangke.fread.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import me.tatarka.inject.annotations.Inject

typealias IosFreadApp = @Composable () -> Unit

@Composable
@Inject
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
