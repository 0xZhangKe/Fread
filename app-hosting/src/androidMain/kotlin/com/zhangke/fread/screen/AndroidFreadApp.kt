package com.zhangke.fread.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.daynight.LocalActivityDayNightHelper
import com.zhangke.fread.common.handler.LocalActivityTextHandler
import com.zhangke.fread.common.language.LocalActivityLanguageHelper
import com.zhangke.fread.common.review.LocalFreadReviewManager
import com.zhangke.fread.common.utils.LocalToastHelper
import com.zhangke.fread.di.AndroidActivityComponent
import com.zhangke.fread.di.AndroidApplicationComponent
import com.zhangke.fread.utils.LocalActivityHelper

@Composable
fun AndroidFreadApp(
    activityComponent: AndroidActivityComponent,
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