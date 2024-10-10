package com.zhangke.fread.activitypub.app.di

import android.content.Context
import androidx.room.Room
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.db.notifications.Notification1to2Migration
import com.zhangke.fread.activitypub.app.internal.db.notifications.NotificationsDatabase
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateDatabases
import com.zhangke.fread.common.di.ApplicationContext
import me.tatarka.inject.annotations.Provides

actual interface ActivityPubPlatformComponent {

    @Provides
    fun provideActivityPubDatabases(
        context: ApplicationContext,
    ): ActivityPubDatabases {
        return Room.databaseBuilder(
            context,
            ActivityPubDatabases::class.java,
            ActivityPubDatabases.DB_NAME,
        ).build()
    }

    @Provides
    fun provideNotificationsDatabase(
        context: ApplicationContext,
    ): NotificationsDatabase {
        return Room.databaseBuilder(
            context,
            NotificationsDatabase::class.java,
            NotificationsDatabase.DB_NAME,
        ).addMigrations(
            Notification1to2Migration()
        ).build()
    }

    @Provides
    fun provideActivityPubStatusDatabase(
        context: ApplicationContext,
    ): ActivityPubStatusDatabases {
        return Room.databaseBuilder(
            context,
            ActivityPubStatusDatabases::class.java,
            ActivityPubStatusDatabases.DB_NAME,
        ).addMigrations(
            ActivityPubStatusDatabases.Status1to2Migration()
        ).build()
    }

    @Provides
    fun provideActivityPubStatusReadStateDatabases(
        context: ApplicationContext,
    ): ActivityPubStatusReadStateDatabases {
        return Room.databaseBuilder(
            context,
            ActivityPubStatusReadStateDatabases::class.java,
            ActivityPubStatusReadStateDatabases.DB_NAME,
        ).build()
    }
}

interface ActivityPubComponentProvider {
    val component: ActivityPubComponent
}

val Context.activityPubComponent get() = (applicationContext as ActivityPubComponentProvider).component
