package com.zhangke.fread.common

import android.content.Context
import com.zhangke.fread.common.status.repo.db.ContentConfigDatabases
import com.zhangke.fread.common.status.repo.db.StatusDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideStatusDatabases(
        @ApplicationContext context: Context
    ): StatusDatabase {
        return StatusDatabase.getInstance(context)
    }

    @Provides
    fun provideContentConfigDatabases(
        @ApplicationContext context: Context
    ): ContentConfigDatabases {
        return ContentConfigDatabases.getInstance(context)
    }
}
