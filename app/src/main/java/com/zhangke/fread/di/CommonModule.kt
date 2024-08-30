package com.zhangke.fread.di

import android.content.Context
import com.zhangke.framework.utils.appContext
import com.zhangke.fread.status.IStatusProvider
import com.zhangke.fread.status.StatusProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    @ApplicationContext
    fun provideApplicationContext(): Context {
        return appContext
    }

    @Provides
    @Singleton
    fun provideProvideStatusProvider(
        providers: Set<@JvmSuppressWildcards IStatusProvider>,
    ): StatusProvider {
        return StatusProvider(providers)
    }

}