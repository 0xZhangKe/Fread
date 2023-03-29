package com.zhangke.utopia.di

import com.zhangke.utopia.status.resolvers.SourceMaintainerResolver
import com.zhangke.utopia.status.resolvers.StatusSourceResolver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class StatusProviderModule {

    @Provides
    fun provideStatusMaintainerResolver(): SourceMaintainerResolver {
        return SourceMaintainerResolver
    }

    @Provides
    fun provideStatusSourceResolver(): StatusSourceResolver {
        return StatusSourceResolver
    }
}