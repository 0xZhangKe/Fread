package com.zhangke.fread.common

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.zhangke.fread.common.db.ContentConfigDatabases
import com.zhangke.fread.common.db.FreadContentDatabase
import com.zhangke.fread.common.db.MixedStatusDatabases
import com.zhangke.fread.common.db.StatusDatabase
import com.zhangke.fread.common.di.ApplicationScope
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDomainMask

actual interface CommonPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideStatusDatabases(): StatusDatabase {
        val dbFilePath = documentDirectory() + "/${StatusDatabase.DB_NAME}"
        return Room.databaseBuilder<StatusDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideContentConfigDatabases(): ContentConfigDatabases {
        val dbFilePath = documentDirectory() + "/${ContentConfigDatabases.DB_NAME}"
        return Room.databaseBuilder<ContentConfigDatabases>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideFreadContentDatabases(): FreadContentDatabase {
        val dbFilePath = documentDirectory() + "/${FreadContentDatabase.DB_NAME}"
        return Room.databaseBuilder<FreadContentDatabase>(name = dbFilePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideMixedStatusDatabases(): MixedStatusDatabases {
        val dbFilePath = documentDirectory() + "/${MixedStatusDatabases.DB_NAME}"
        return Room.databaseBuilder<MixedStatusDatabases>(name = dbFilePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    @ApplicationScope
    @Provides
    fun provideFlowSettings(delegate: NSUserDefaults): FlowSettings {
        return NSUserDefaultsSettings(delegate).toFlowSettings()
    }
}

// From: https://developer.android.com/kotlin/multiplatform/room#ios
@OptIn(ExperimentalForeignApi::class)
fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}