package com.zhangke.utopia.activitypub.app.internal.di

import android.content.Context
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.utopia.activitypub.app.internal.db.notifications.NotificationsDatabase
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusReadStateDatabases
import com.zhangke.utopia.activitypub.app.internal.db.status.ExpiredActivityPubStatusDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal class CommonModules {

    @Provides
    fun provideActivityPubDatabases(@ApplicationContext context: Context): ActivityPubDatabases {
        return ActivityPubDatabases.getInstance(context)
    }

    @Provides
    fun provideExpiredActivityPubStatusDatabase(@ApplicationContext context: Context): ExpiredActivityPubStatusDatabase {
        return ExpiredActivityPubStatusDatabase.getInstance(context)
    }

    @Provides
    fun provideNotificationsDatabase(@ApplicationContext context: Context): NotificationsDatabase {
        return NotificationsDatabase.getInstance(context)
    }

    @Provides
    fun provideActivityPubStatusDatabase(@ApplicationContext context: Context): ActivityPubStatusDatabases {
        return ActivityPubStatusDatabases.getInstance(context)
    }

    @Provides
    fun provideActivityPubStatusReadStateDatabases(
        @ApplicationContext context: Context,
    ): ActivityPubStatusReadStateDatabases {
        return ActivityPubStatusReadStateDatabases.getInstance(context)
    }
}
