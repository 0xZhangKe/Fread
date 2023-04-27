package com.zhangke.utopia.activitypubapp.di

import com.zhangke.utopia.activitypubapp.db.ActivityPubDatabases
import com.zhangke.utopia.activitypubapp.source.user.UserSourceRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class CommonModules {

    @Provides
    fun provideUserSourceRepo(): UserSourceRepo {
        return UserSourceRepo
    }

    @Provides
    fun provideActivityPubDatabases(): ActivityPubDatabases {
        return ActivityPubDatabases.instance
    }
}