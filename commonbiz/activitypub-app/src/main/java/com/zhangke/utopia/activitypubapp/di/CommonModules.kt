package com.zhangke.utopia.activitypubapp.di

import com.zhangke.utopia.activitypubapp.db.ActivityPubDatabases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class CommonModules {

    @Provides
    fun provideActivityPubDatabases(): ActivityPubDatabases {
        return ActivityPubDatabases.instance
    }
}
