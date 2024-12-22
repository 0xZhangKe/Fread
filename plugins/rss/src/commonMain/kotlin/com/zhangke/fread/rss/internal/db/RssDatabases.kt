package com.zhangke.fread.rss.internal.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.zhangke.fread.rss.internal.db.converter.FormalUriConverter
import com.zhangke.fread.rss.internal.db.converter.InstantConverter


private const val DB_VERSION = 1

@TypeConverters(
    FormalUriConverter::class,
    InstantConverter::class,
)
@Database(
    entities = [RssChannelEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
@ConstructedBy(RssDatabasesConstructor::class)
abstract class RssDatabases : RoomDatabase() {

    abstract fun getRssChannelDao(): RssChannelDao

    companion object {
        const val DB_NAME = "rss.db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object RssDatabasesConstructor : RoomDatabaseConstructor<RssDatabases> {
    override fun initialize(): RssDatabases
}
