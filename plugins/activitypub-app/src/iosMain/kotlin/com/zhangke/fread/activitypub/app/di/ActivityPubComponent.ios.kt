package com.zhangke.fread.activitypub.app.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusDatabases
import com.zhangke.fread.activitypub.app.internal.db.status.ActivityPubStatusReadStateDatabases
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

    @Provides
    fun provideActivityPubStatusDatabase(): ActivityPubStatusDatabases {
        val dbFilePath = documentDirectory() + "/${ActivityPubStatusDatabases.DB_NAME}"
        return Room.databaseBuilder<ActivityPubStatusDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addMigrations(
                ActivityPubStatusDatabases.Status1to2Migration(),
                ActivityPubStatusDatabases.Status1to3Migration(),
                ActivityPubStatusDatabases.Status2to3Migration(),
            )
            .build()
    }

    @Provides
    fun provideActivityPubStatusReadStateDatabases(): ActivityPubStatusReadStateDatabases {
        val dbFilePath = documentDirectory() + "/${ActivityPubStatusReadStateDatabases.DB_NAME}"
        return Room.databaseBuilder<ActivityPubStatusReadStateDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addMigrations(
                ActivityPubStatusReadStateDatabases.StatusReadState1to2Migration(),
            )
            .build()
    }
}