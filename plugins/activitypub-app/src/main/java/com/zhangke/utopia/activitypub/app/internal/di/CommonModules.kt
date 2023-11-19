package com.zhangke.utopia.activitypub.app.internal.di

import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
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
