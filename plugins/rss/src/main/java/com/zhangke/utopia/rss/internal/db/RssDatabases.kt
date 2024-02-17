package com.zhangke.utopia.rss.internal.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.utopia.rss.internal.db.converter.FormalUriConverter

private const val DB_NAME = "rss.db"
private const val DB_VERSION = 1

@TypeConverters(
    FormalUriConverter::class,
)
@Database(
    entities = [RssChannelEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class RssDatabases : RoomDatabase() {

    abstract fun getRssChannelDao(): RssChannelDao

    companion object{

        private var instance: RssDatabases? = null

        fun getInstance(context: Context): RssDatabases {
            if (instance == null) {
                synchronized(RssDatabases::class.java) {
                    if (instance == null) {
                        instance = createDatabase(context)
                    }
                }
            }
            return instance!!
        }

        private fun createDatabase(context: Context): RssDatabases {
            return Room.databaseBuilder(
                context,
                RssDatabases::class.java,
                DB_NAME,
            ).build()
        }
    }
}
