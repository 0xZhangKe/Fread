package com.zhangke.fread.di

import android.app.Activity
import android.content.Context
import com.zhangke.fread.common.CommonUiComponent
import com.zhangke.fread.common.di.ActivityScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@ActivityScope
abstract class ActivityComponent(
    @Component val applicationComponent: ApplicationComponent,
    @get:Provides val activity: Activity,
) : CommonUiComponent {

    @Provides
    @ActivityScope
    fun provideActivityContext(): Context {
        return activity
    }

    companion object
}