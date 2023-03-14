package com.zhangke.utopia.di

import android.content.Context
import com.zhangke.framework.utils.appContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    @ApplicationContext
    fun provideApplicationContext(): Context {
        return appContext
    }
}