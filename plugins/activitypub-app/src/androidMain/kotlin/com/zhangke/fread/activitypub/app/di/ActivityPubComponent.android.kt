package com.zhangke.fread.activitypub.app.di

import android.content.Context
import androidx.room.Room
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateDatabases
import com.zhangke.fread.activitypub.app.internal.push.PushInfoDatabase
import com.zhangke.fread.activitypub.app.internal.push.PushInfoRepo
import com.zhangke.fread.activitypub.app.internal.push.notification.PushNotificationManager
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Provides

actual interface ActivityPubPlatformComponent {

    val pushNotificationManager: PushNotificationManager

    @ApplicationScope
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

    @ApplicationScope
    @Provides
    fun provideActivityPubStatusDatabase(
        context: ApplicationContext,
    ): ActivityPubStatusDatabases {
        return Room.databaseBuilder(
            context,
            ActivityPubStatusDatabases::class.java,
            ActivityPubStatusDatabases.DB_NAME,
        ).addMigrations(ActivityPubStatusDatabases.MIGRATION_1_2)
            .build()
    }

    @ApplicationScope
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

    @ApplicationScope
    @Provides
    fun provideActivityPushDatabase(
        context: ApplicationContext,
    ): PushInfoDatabase {
        return Room.databaseBuilder(
            context,
            PushInfoDatabase::class.java,
            PushInfoDatabase.DB_NAME,
        ).build()
    }

    @ApplicationScope
    @Provides
    fun providePushInfoRepo(database: PushInfoDatabase): PushInfoRepo {
        return PushInfoRepo(database)
    }
}

interface ActivityPubComponentProvider {
    val component: ActivityPubComponent
}

val Context.activityPubComponent get() = (applicationContext as ActivityPubComponentProvider).component
