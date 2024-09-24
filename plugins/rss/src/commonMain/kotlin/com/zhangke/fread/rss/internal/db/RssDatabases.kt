package com.zhangke.fread.rss.internal.db

import androidx.room.Database
import androidx.room.RoomDatabase
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
abstract class RssDatabases : RoomDatabase() {

    abstract fun getRssChannelDao(): RssChannelDao

    companion object {
        const val DB_NAME = "rss.db"
    }
}
