package com.zhangke.utopia.common.status.repo.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.common.feeds.repo.FeedsEntity

private const val DB_NAME = "StatusDatabase.db"
private const val DB_VERSION = 1

@Database(
    entities = [StatusSourceEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class StatusDatabase : RoomDatabase() {

    abstract fun getSourceDao(): StatusSourceDao

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
