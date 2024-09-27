package com.zhangke.fread.di

import android.app.Activity
import androidx.activity.ComponentActivity
import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ActivityScope
abstract class AndroidActivityComponent(
    @Component val applicationComponent: AndroidApplicationComponent,
    @get:Provides val activity: ComponentActivity,
) : HostingActivityComponent {

    @Provides
    fun provideActivity(): Activity = activity

    companion object
}