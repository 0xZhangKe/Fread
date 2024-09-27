package com.zhangke.fread.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.di.IosActivityComponent

@Composable
internal fun IosFreadApp(
    activityComponent: IosActivityComponent,
) {
    CompositionLocalProvider(
        LocalActivityBrowserLauncher provides activityComponent.activityBrowserLauncher,
    ) {
        FreadApp(
            applicationComponent = activityComponent.applicationComponent,
            activityComponent = activityComponent,
        )
    }
}