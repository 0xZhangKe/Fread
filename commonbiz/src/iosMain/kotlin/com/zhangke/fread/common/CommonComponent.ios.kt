package com.zhangke.fread.common

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.zhangke.fread.common.browser.BrowserLauncher
import com.zhangke.fread.common.browser.IosBrowserLauncher
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.status.repo.db.ContentConfigDatabases
import com.zhangke.fread.common.status.repo.db.StatusDatabase
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
    fun provideFlowSettings(delegate: NSUserDefaults): FlowSettings {
        return NSUserDefaultsSettings(delegate).toFlowSettings()
    }

    @Provides
    fun IosBrowserLauncher.binds(): BrowserLauncher = this
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