package com.zhangke.utopia.common.status.repo.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.utopia.common.status.repo.db.converts.BlogMediaListConverter
import com.zhangke.utopia.common.status.repo.db.converts.BlogPollConverter
import com.zhangke.utopia.common.status.repo.db.converts.StatusConverter
import com.zhangke.utopia.common.status.repo.db.converts.StatusProviderUriConverter
import com.zhangke.utopia.common.status.repo.db.converts.StatusProviderUriListConverter
import com.zhangke.utopia.common.status.repo.db.converts.StatusTypeConverter
import com.zhangke.utopia.common.utils.DateTypeConverter
import com.zhangke.utopia.common.utils.ListStringConverter
import com.zhangke.utopia.common.utils.WebFingerConverter
import dagger.hilt.android.qualifiers.ApplicationContext

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
    StatusConverter::class,
)
@Database(
    entities = [
        StatusSourceEntity::class,
        FeedsConfigEntity::class,
        StatusContentEntity::class,
    ],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class StatusDatabase : RoomDatabase() {

    abstract fun getSourceDao(): StatusSourceDao

    abstract fun getFeedsConfigDao(): FeedsConfigDao

    abstract fun getStatusContentDao(): StatusContentDao

    companion object {

        private var instance: StatusDatabase? = null

        fun getInstance(context: Context): StatusDatabase {
            if (instance == null) {
                synchronized(StatusDatabase::class.java) {
                    if (instance == null) {
                        instance = createDatabase(context)
                    }
                }
            }
            return instance!!
        }

        private fun createDatabase(context: Context): StatusDatabase {
            return Room.databaseBuilder(
                context,
                StatusDatabase::class.java,
                DB_NAME,
            ).build()
        }
    }
}
