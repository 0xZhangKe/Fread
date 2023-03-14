package com.zhangke.utopia.db

import android.content.Context
import com.zhangke.utopia.di.ApplicationContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DatabaseRepoModule {

    @Provides
    fun provideChannelDB(@ApplicationContext context: Context): ChannelsDatabases {
        return ChannelsDatabases.createDatabase(context)
    }

    @Provides
    fun provideChannelRepo(channelsDatabases: ChannelsDatabases): ChannelRepo {
        return ChannelRepo(channelsDatabases)
    }
}