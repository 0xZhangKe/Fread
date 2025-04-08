package com.zhangke.fread.feature.message.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.zhangke.fread.common.documentDirectory
import com.zhangke.fread.feature.message.repo.notification.NotificationsDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides

actual interface NotificationsComponentPlatform {

    @Provides
    fun provideNotificationsDatabase(): NotificationsDatabase {
        val dbFilePath = documentDirectory() + "/${NotificationsDatabase.DB_NAME}"
        return Room.databaseBuilder<NotificationsDatabase>(name = dbFilePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
