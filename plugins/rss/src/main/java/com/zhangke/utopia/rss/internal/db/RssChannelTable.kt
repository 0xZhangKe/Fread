package com.zhangke.utopia.rss.internal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.Date

private const val TABLE_NAME = "channels"

@Entity(tableName = TABLE_NAME)
data class RssChannelEntity(
    @PrimaryKey val url: String,
    val homePage: String?,
    val title: String,
    val description: String?,
    val displayName: String,
    val addDate: Date,
    val lastUpdateDate: Date,
    val lastBuildDate: Date?,
    val updatePeriod: String?,
    val thumbnail: String?,
)

@Dao
interface RssChannelDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<RssChannelEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE url=:url")
    suspend fun queryByUrl(url: String): RssChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RssChannelEntity)

    @Delete
    suspend fun delete(entity: RssChannelEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE url=:url")
    suspend fun deleteByUri(url: String)
}
