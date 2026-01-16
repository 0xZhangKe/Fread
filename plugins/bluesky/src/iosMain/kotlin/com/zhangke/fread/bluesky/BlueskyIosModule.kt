package com.zhangke.fread.bluesky

import androidx.room.Room
import com.zhangke.fread.bluesky.internal.db.BlueskyLoggedAccountDatabase
import com.zhangke.fread.common.documentDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.core.module.Module

actual fun Module.createPlatformModule() {

    single<BlueskyLoggedAccountDatabase> {
        val dbFilePath = getDBFilePath(BlueskyLoggedAccountDatabase.DB_NAME)
        Room.databaseBuilder<BlueskyLoggedAccountDatabase>(
            name = dbFilePath,
        ).setDriver(androidx.sqlite.driver.bundled.BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}

private fun getDBFilePath(dbName: String): String {
    return documentDirectory() + "/$dbName"
}
