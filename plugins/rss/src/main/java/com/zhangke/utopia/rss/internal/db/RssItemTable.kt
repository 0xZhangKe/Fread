package com.zhangke.utopia.rss.internal.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.Date
//
//private const val TABLE_NAME = "items"
//
//@Entity(tableName = TABLE_NAME)
//data class RssItemEntity(
//    @PrimaryKey val id: String,
//    val rssSourceUrl: String,
//    val title: String,
//    val author: String?,
//    val link: String?,
//    val pubDate: Date,
//    val description: String?,
//    val content: String?,
//    val image: String?,
//    val audio: String?,
//    val video: String?,
//    val sourceName: String?,
//    val categories: List<String>,
//    val commentsUrl: String?,
//)
//
//@Dao
//interface RssItemDao {
//
//    @Query("SELECT * FROM $TABLE_NAME WHERE rssSourceUrl = :rssSourceUrl ORDER BY pubDate DESC")
//    suspend fun queryBySourceUrl(rssSourceUrl: String): List<RssItemEntity>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insert(item: RssItemEntity)
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    suspend fun insertList(items: List<RssItemEntity>)
//
//    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
//    suspend fun deleteById(id: String)
//
//    @Query("DELETE FROM $TABLE_NAME WHERE rssSourceUrl = :sourceUrl")
//    suspend fun deleteBySourceUrl(sourceUrl: String)
//}