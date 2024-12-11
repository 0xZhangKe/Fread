package com.zhangke.fread.bluesky

import androidx.room.Room
import com.zhangke.fread.bluesky.internal.db.BlueskyLoggedAccountDatabase
import com.zhangke.fread.common.di.ApplicationContext
import com.zhangke.fread.common.di.ApplicationScope
import me.tatarka.inject.annotations.Provides

actual interface BlueskyPlatformComponent {

    @ApplicationScope
    @Provides
    fun provideBskyLoggedAccountDatabases(
        context: ApplicationContext,
    ): BlueskyLoggedAccountDatabase {
        return Room.databaseBuilder(
            context,
            BlueskyLoggedAccountDatabase::class.java,
            BlueskyLoggedAccountDatabase.DB_NAME,
        ).build()
    }
}
