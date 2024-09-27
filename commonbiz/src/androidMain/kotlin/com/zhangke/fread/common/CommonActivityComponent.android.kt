package com.zhangke.fread.common

import android.app.Activity
import com.zhangke.fread.common.browser.ActivityBrowserLauncher
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Provides

actual interface CommonActivityPlatformComponent {
    val activityBrowserLauncher: ActivityBrowserLauncher

    @ActivityScope
    @Provides
    fun provideActivityBrowserLauncher(activity: Activity): ActivityBrowserLauncher {
        return BrowserLauncher(activity)
    }
}