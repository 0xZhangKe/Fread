package com.zhangke.utopia.common.feeds.repo

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.utopia.common.utils.ListStringConverter

private const val DB_NAME = "UtopiaFeeds.db"
private const val TABLE_NAME = "feeds"
private const val DB_VERSION = 1

@TypeConverters(ListStringConverter::class)
@Entity(tableName = TABLE_NAME)
data class FeedsEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val uriList: List<String>,
)

@Dao
interface FeedsDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<FeedsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(feedsEntity: FeedsEntity)

    @Query("SELECT name FROM $TABLE_NAME")
    suspend fun queryAllNames(): List<String>

    @Query("SELECT * FROM $TABLE_NAME WHERE id=:id")
    suspend fun queryById(id: Int): FeedsEntity?

    @Query("DELETE FROM $TABLE_NAME WHERE id=:id")
    suspend fun delete(id: Int)
}

@Database(entities = [FeedsEntity::class], version = DB_VERSION, exportSchema = false)
abstract class FeedsDatabases : RoomDatabase() {

    abstract fun getDao(): FeedsDao

    companion object {

        fun createDatabase(context: Context): FeedsDatabases {
            return Room.databaseBuilder(context, FeedsDatabases::class.java, DB_NAME)
                .build()
        }
    }
}
