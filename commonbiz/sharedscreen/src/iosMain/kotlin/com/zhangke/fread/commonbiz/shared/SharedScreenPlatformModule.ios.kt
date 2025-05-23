package com.zhangke.fread.commonbiz.shared

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.documentDirectory
import com.zhangke.fread.commonbiz.shared.db.SelectedAccountPublishingDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides

actual interface SharedScreenPlatformModule {

    @ApplicationScope
    @Provides
    fun provideSelectedAccountPublishingDatabase(): SelectedAccountPublishingDatabase {
        val dbFilePath = documentDirectory() + "/${SelectedAccountPublishingDatabase.DB_NAME}"
        return Room.databaseBuilder<SelectedAccountPublishingDatabase>(name = dbFilePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
