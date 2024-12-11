package com.zhangke.fread.bluesky

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.zhangke.fread.bluesky.internal.db.BlueskyLoggedAccountDatabase
import com.zhangke.fread.common.di.ApplicationScope
import com.zhangke.fread.common.documentDirectory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides

actual interface BlueskyPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideBskyLoggedAccountDatabases(): BlueskyLoggedAccountDatabase {
        val dbFilePath = documentDirectory() + "/${BlueskyLoggedAccountDatabase.DB_NAME}"
        return Room.databaseBuilder<BlueskyLoggedAccountDatabase>(
            name = dbFilePath,
        ).setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}
