package com.zhangke.utopia.common

import android.content.Context
import com.zhangke.utopia.common.status.repo.db.StatusDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    @Provides
    fun provideStatusDatabase(@ApplicationContext context: Context): StatusDatabase {
        return StatusDatabase.getInstance(context)
    }
}
