package com.zhangke.fread.di

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import com.zhangke.fread.common.CommonActivityComponent
import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ActivityScope
abstract class ActivityComponent(
    @Component val applicationComponent: ApplicationComponent,
    @get:Provides val activity: ComponentActivity,
) : CommonActivityComponent {

    @Provides
    fun provideActivity(): Activity = activity

    companion object
}