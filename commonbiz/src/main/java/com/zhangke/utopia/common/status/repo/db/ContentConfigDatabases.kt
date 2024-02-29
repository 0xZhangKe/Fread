package com.zhangke.utopia.common.status.repo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.utopia.common.status.repo.db.converts.ContentTabConverter
import com.zhangke.utopia.common.status.repo.db.converts.ContentTypeConverter
import com.zhangke.utopia.common.status.repo.db.converts.FormalBaseUrlConverter
import com.zhangke.utopia.common.status.repo.db.converts.StatusProviderUriConverter
import com.zhangke.utopia.common.status.repo.db.converts.StatusProviderUriListConverter

private const val DB_NAME = "ContentConfig.db"
private const val DB_VERSION = 1

@TypeConverters(
    ContentTypeConverter::class,
    StatusProviderUriConverter::class,
    StatusProviderUriListConverter::class,
    FormalBaseUrlConverter::class,
    ContentTabConverter::class,
)
@Database(
    entities = [ContentConfigEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class ContentConfigDatabases : RoomDatabase() {

    abstract fun getContentConfigDao(): ContentConfigDao

    companion object {

        private var instance: ContentConfigDatabases? = null

        fun getInstance(context: Context): ContentConfigDatabases {
            if (instance == null) {
                synchronized(ContentConfigDatabases::class.java) {
                    if (instance == null) {
                        instance = createDatabase(context)
                    }
                }
            }
            return instance!!
        }

        private fun createDatabase(context: Context): ContentConfigDatabases {
            return Room.databaseBuilder(
                context,
                ContentConfigDatabases::class.java,
                DB_NAME,
            ).build()
        }
    }
}
