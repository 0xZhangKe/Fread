package com.zhangke.utopia.common.status.repo.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.common.utils.DateTypeConverter
import com.zhangke.utopia.common.utils.ListStringConverter
import com.zhangke.utopia.common.utils.WebFingerConverter

private const val DB_NAME = "StatusDatabase.db"
private const val DB_VERSION = 1

@TypeConverters(
    StatusProviderUriConverter::class,
    ListStringConverter::class,
    StatusProviderUriListConverter::class,
    StatusTypeConverter::class,
    BlogMediaListConverter::class,
    BlogPollConverter::class,
    DateTypeConverter::class,
    WebFingerConverter::class,
)
@Database(
    entities = [StatusSourceEntity::class, FeedsConfigEntity::class, StatusContentEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class StatusDatabase : RoomDatabase() {

    abstract fun getSourceDao(): StatusSourceDao

    abstract fun getFeedsConfigDao(): FeedsConfigDao

    abstract fun getStatusContentDao(): StatusContentDao

    companion object {

        val instance: StatusDatabase by lazy { createDatabase() }

        private fun createDatabase(): StatusDatabase {
            return Room.databaseBuilder(
                appContext,
                StatusDatabase::class.java,
                DB_NAME,
            ).build()
        }
    }
}
