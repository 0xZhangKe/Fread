package com.zhangke.fread.feature.message.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.zhangke.fread.common.documentDirectory
import com.zhangke.fread.feature.message.repo.notification.NotificationsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module

actual fun Module.createPlatformModule() {
    factory<NotificationsDatabase> {
        val dbFilePath = getDBFilePath(NotificationsDatabase.DB_NAME)
        Room.databaseBuilder<NotificationsDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

private fun getDBFilePath(dbName: String): String {
    return documentDirectory() + "/$dbName"
}
