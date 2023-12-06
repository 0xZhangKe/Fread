package com.zhangke.utopia.common

import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideStatusDatabase(): StatusDatabase {
        return StatusDatabase.instance
    }
}
