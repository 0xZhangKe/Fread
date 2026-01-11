package com.zhangke.fread.commonbiz.shared

import androidx.room.Room
import com.zhangke.fread.commonbiz.shared.db.SelectedAccountPublishingDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

actual fun Module.createPlatformModule() {
    single<SelectedAccountPublishingDatabase> {
        Room.databaseBuilder(
            androidContext(),
            SelectedAccountPublishingDatabase::class.java,
            SelectedAccountPublishingDatabase.DB_NAME,
        ).build()
    }
}
