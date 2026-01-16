package com.zhangke.fread.commonbiz.shared

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.zhangke.fread.common.documentDirectory
import com.zhangke.fread.commonbiz.shared.db.SelectedAccountPublishingDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module

actual fun Module.createPlatformModule() {
    single<SelectedAccountPublishingDatabase> {
        val dbFilePath = getDBFilePath(SelectedAccountPublishingDatabase.DB_NAME)
        Room.databaseBuilder<SelectedAccountPublishingDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

private fun getDBFilePath(dbName: String): String {
    return documentDirectory() + "/$dbName"
}
