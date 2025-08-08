package com.zhangke.fread.feature.message.di

import androidx.room.Room
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.feature.message.repo.notification.NotificationsDatabase
import me.tatarka.inject.annotations.Provides

actual interface NotificationsComponentPlatform {

    @ApplicationScope
    @Provides
    fun provideNotificationsDatabase(
        context: ApplicationContext,
    ): NotificationsDatabase {
        return Room.databaseBuilder(
            context,
            NotificationsDatabase::class.java,
            NotificationsDatabase.DB_NAME,
        ).addMigrations(NotificationsDatabase.MIGRATION_1_2).build()
    }
}
