package com.zhangke.fread.activitypub.app.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountDatabase
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateDatabases
import com.zhangke.fread.activitypub.app.internal.push.ActivityPubPushManager
import com.zhangke.fread.common.documentDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf

actual fun Module.createPlatformModule() {
    factory<ActivityPubDatabases> {
        val dbFilePath = getDBFilePath(ActivityPubDatabases.DB_NAME)
        Room.databaseBuilder<ActivityPubDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<ActivityPubLoggedAccountDatabase> {
        val dbFilePath = getDBFilePath(ActivityPubLoggedAccountDatabase.DB_NAME)
        Room.databaseBuilder<ActivityPubLoggedAccountDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    factory<ActivityPubStatusDatabases> {
        val dbFilePath = getDBFilePath(ActivityPubStatusDatabases.DB_NAME)
        Room.databaseBuilder<ActivityPubStatusDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addMigrations(ActivityPubStatusDatabases.MIGRATION_1_2)
            .build()
    }
    factory<ActivityPubStatusReadStateDatabases> {
        val dbFilePath = getDBFilePath(ActivityPubStatusReadStateDatabases.DB_NAME)
        Room.databaseBuilder<ActivityPubStatusReadStateDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    factoryOf(::ActivityPubPushManager)
}

private fun getDBFilePath(dbName: String): String {
    return documentDirectory() + "/$dbName"
}
