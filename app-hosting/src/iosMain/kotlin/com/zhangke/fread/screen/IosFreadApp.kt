package com.zhangke.fread.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.zhangke.fread.common.browser.ActivityBrowserLauncher
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import me.tatarka.inject.annotations.Inject

typealias IosFreadApp = @Composable () -> Unit

@Composable
@Inject
internal fun IosFreadApp(
    activityBrowserLauncher: ActivityBrowserLauncher,
    freadApp: FreadApp,
) {
    CompositionLocalProvider(
        LocalActivityBrowserLauncher provides activityBrowserLauncher,
    ) {
        freadApp()
    }
}
