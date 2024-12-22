package com.zhangke.fread.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.zhangke.fread.common.browser.ActivityBrowserLauncher
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import me.tatarka.inject.annotations.Inject

typealias AndroidFreadApp = @Composable () -> Unit

@Composable
@Inject
fun AndroidFreadApp(
    activityBrowserLauncher: ActivityBrowserLauncher,
    freadApp: FreadApp,
) {
    CompositionLocalProvider(
        LocalActivityBrowserLauncher provides activityBrowserLauncher,
    ) {
        freadApp()
    }
}