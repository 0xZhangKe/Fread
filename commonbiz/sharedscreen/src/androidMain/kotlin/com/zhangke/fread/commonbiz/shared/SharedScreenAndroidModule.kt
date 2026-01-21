package com.zhangke.fread.commonbiz.shared

import androidx.room.Room
import com.zhangke.framework.nav.NavEntryProvider
import com.zhangke.fread.commonbiz.shared.db.SelectedAccountPublishingDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind

actual fun Module.createPlatformModule() {
    single<SelectedAccountPublishingDatabase> {
        Room.databaseBuilder(
            androidContext(),
            SelectedAccountPublishingDatabase::class.java,
            SelectedAccountPublishingDatabase.DB_NAME,
        ).build()
    }
    factoryOf(::SharedScreenAndroidEntryProvider) bind NavEntryProvider::class
}
