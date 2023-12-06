package com.zhangke.utopia.common

import com.zhangke.utopia.common.feeds.repo.config.FeedsConfigDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideFeedsConfigDataBase(): FeedsConfigDatabase {
        return FeedsConfigDatabase.instance
    }
}
