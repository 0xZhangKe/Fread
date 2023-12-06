package com.zhangke.utopia.common.feeds.repo.config

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.common.utils.ListStringConverter

private const val DB_NAME = "feeds_configs.db"
private const val DB_VERSION = 1
private const val TABLE_NAME = "feeds_configs"

@Entity(tableName = TABLE_NAME)
data class FeedsConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val authorUserId: String,
    val name: String,
    val sourceUriList: List<String>,
    val databaseFilePath: String?,
)

@Dao
interface FeedsConfigDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAllFeedsConfig(): List<FeedsConfigEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE authorUserId=:authorUserId")
    suspend fun queryFeedsConfigByUserId(authorUserId: String): List<FeedsConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: FeedsConfigEntity)

    @Delete
    suspend fun delete(entity: FeedsConfigEntity)
}

@TypeConverters(
    ListStringConverter::class
)
@Database(
    entities = [FeedsConfigEntity::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class FeedsConfigDatabase : RoomDatabase() {

    abstract fun getDao(): FeedsConfigDao

    companion object {

        val instance: FeedsConfigDatabase by lazy { createDatabase() }

        private fun createDatabase(): FeedsConfigDatabase {
            return Room.databaseBuilder(
                appContext,
                FeedsConfigDatabase::class.java,
                DB_NAME
            ).build()
        }
    }
}
