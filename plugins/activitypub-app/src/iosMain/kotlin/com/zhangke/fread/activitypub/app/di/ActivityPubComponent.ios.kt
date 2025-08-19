package com.zhangke.fread.activitypub.app.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountDatabase
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateDatabases
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.documentDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides

actual interface ActivityPubPlatformComponent {
    @Provides
    fun provideActivityPubDatabases(): ActivityPubDatabases {
        val dbFilePath = documentDirectory() + "/${ActivityPubDatabases.DB_NAME}"
        return Room.databaseBuilder<ActivityPubDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideActivityPubLoggedAccountDatabases(): ActivityPubLoggedAccountDatabase {
        val dbFilePath = documentDirectory() + "/${ActivityPubLoggedAccountDatabase.DB_NAME}"
        return Room.databaseBuilder<ActivityPubLoggedAccountDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @Provides
    fun provideActivityPubStatusDatabase(): ActivityPubStatusDatabases {
        val dbFilePath = documentDirectory() + "/${ActivityPubStatusDatabases.DB_NAME}"
        return Room.databaseBuilder<ActivityPubStatusDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addMigrations(ActivityPubStatusDatabases.MIGRATION_1_2)
            .build()
    }

    @Provides
    fun provideActivityPubStatusReadStateDatabases(): ActivityPubStatusReadStateDatabases {
        val dbFilePath = documentDirectory() + "/${ActivityPubStatusReadStateDatabases.DB_NAME}"
        return Room.databaseBuilder<ActivityPubStatusReadStateDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}