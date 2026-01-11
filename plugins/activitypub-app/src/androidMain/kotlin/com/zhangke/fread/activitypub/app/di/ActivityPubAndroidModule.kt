package com.zhangke.fread.activitypub.app.di

import androidx.room.Room
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountDatabase
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateDatabases
import com.zhangke.fread.activitypub.app.internal.push.ActivityPubPushManager
import com.zhangke.fread.activitypub.app.internal.push.PushInfoDatabase
import com.zhangke.fread.activitypub.app.internal.push.PushInfoRepo
import com.zhangke.fread.activitypub.app.internal.push.notification.PushNotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf

actual fun Module.createPlatformModule() {
    single<ActivityPubDatabases> {
        Room.databaseBuilder(
            androidContext(),
            ActivityPubDatabases::class.java,
            ActivityPubDatabases.DB_NAME,
        ).build()
    }
    single<ActivityPubLoggedAccountDatabase> {
        Room.databaseBuilder(
            androidContext(),
            ActivityPubLoggedAccountDatabase::class.java,
            ActivityPubLoggedAccountDatabase.DB_NAME,
        ).build()
    }
    single<ActivityPubStatusDatabases> {
        Room.databaseBuilder(
            androidContext(),
            ActivityPubStatusDatabases::class.java,
            ActivityPubStatusDatabases.DB_NAME,
        ).addMigrations(ActivityPubStatusDatabases.MIGRATION_1_2)
            .build()
    }
    single<ActivityPubStatusReadStateDatabases> {
        Room.databaseBuilder(
            androidContext(),
            ActivityPubStatusReadStateDatabases::class.java,
            ActivityPubStatusReadStateDatabases.DB_NAME,
        ).build()
    }
    single<PushInfoDatabase> {
        Room.databaseBuilder(
            androidContext(),
            PushInfoDatabase::class.java,
            PushInfoDatabase.DB_NAME,
        ).build()
    }
    singleOf(::PushInfoRepo)
    factoryOf(::PushNotificationManager)
    factoryOf(::ActivityPubPushManager)
}
