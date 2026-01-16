package com.zhangke.fread.bluesky

import androidx.room.Room
import com.zhangke.fread.bluesky.internal.db.BlueskyLoggedAccountDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

actual fun Module.createPlatformModule() {
    single<BlueskyLoggedAccountDatabase> {
        Room.databaseBuilder(
            androidContext(),
            BlueskyLoggedAccountDatabase::class.java,
            BlueskyLoggedAccountDatabase.DB_NAME,
        ).build()
    }
}
