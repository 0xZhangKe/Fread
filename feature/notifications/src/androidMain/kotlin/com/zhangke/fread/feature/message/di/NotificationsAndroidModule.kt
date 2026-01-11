package com.zhangke.fread.feature.message.di

import androidx.room.Room
import com.zhangke.fread.feature.message.repo.notification.NotificationsDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

actual fun Module.createPlatformModule() {
    single<NotificationsDatabase> {
        Room.databaseBuilder(
            androidContext(),
            NotificationsDatabase::class.java,
            NotificationsDatabase.DB_NAME,
        ).addMigrations(NotificationsDatabase.MIGRATION_1_2).build()
    }
}
