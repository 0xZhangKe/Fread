package com.zhangke.utopia.feeds.repo.db

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
import com.zhangke.framework.room.ListStringConverter

private const val DB_NAME = "UtopiaFeeds.db"
private const val TABLE_NAME = "feeds"
private const val DB_VERSION = 1

@TypeConverters(ListStringConverter::class)
@Entity(tableName = TABLE_NAME)
internal data class FeedsEntity(
    @PrimaryKey val name: String,
    val uriList: List<String>,
)

@Dao
internal interface FeedsDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<FeedsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(feedsEntity: FeedsEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE name=:feedsName")
    suspend fun delete(feedsName: String)
}

@Database(entities = [FeedsEntity::class], version = DB_VERSION, exportSchema = false)
internal abstract class FeedsDatabases : RoomDatabase() {

    abstract fun getDao(): FeedsDao

    companion object {

        fun createDatabase(context: Context): FeedsDatabases {
            return Room.databaseBuilder(context, FeedsDatabases::class.java, DB_NAME)
                .build()
        }
    }
}
