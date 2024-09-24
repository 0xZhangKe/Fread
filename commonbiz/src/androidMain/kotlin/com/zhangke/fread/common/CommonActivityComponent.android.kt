package com.zhangke.fread.common

import android.app.Activity
import com.zhangke.fread.common.browser.ActivityBrowserLauncher
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.daynight.ActivityDayNightHelper
import com.zhangke.fread.common.di.ActivityScope
import com.zhangke.fread.common.language.ActivityLanguageHelper
import me.tatarka.inject.annotations.Provides

actual interface CommonActivityPlatformComponent {
    val activityLanguageHelper: ActivityLanguageHelper
    val activityDayNightHelper: ActivityDayNightHelper

    val activityBrowserLauncher: ActivityBrowserLauncher

    @ActivityScope
    @Provides
    fun provideActivityBrowserLauncher(activity: Activity): ActivityBrowserLauncher {
        return BrowserLauncher(activity)
    }
}