package com.zhangke.fread.common.browser

import android.app.Activity
import androidx.activity.ComponentActivity
import com.zhangke.fread.common.CommonComponent
import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ActivityScope
internal abstract class BrowserBridgeDialogActivityComponent(
    @get:Provides val commonComponent: CommonComponent,
    @get:Provides val activity: ComponentActivity,
) {

    @Provides
    fun provideActivity(): Activity = activity

    @Provides
    fun provideBrowserLauncher(): BrowserLauncher = commonComponent.browserLauncher

    @Provides
    fun AndroidActivityBrowserLauncher.binds(): ActivityBrowserLauncher = this

    abstract val activityBrowserLauncher: ActivityBrowserLauncher

    companion object
}