package com.zhangke.utopia.common.status.repo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.status.blog.BlogMedia
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.model.StatusType
import com.zhangke.utopia.status.uri.StatusProviderUri
import java.util.Date

private const val TABLE_NAME = "status_content"

@Entity(tableName = TABLE_NAME)
data class StatusContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val authorUri: StatusProviderUri,
    val authorWebFinger: WebFinger,
    val authorName: String,
    val authorDescription: String,
    val authorAvatar: String?,
    val sourceUri: StatusProviderUri,
    val type: StatusType,
    val statusId: String,
    val title: String?,
    val content: String,
    val date: Date,
    val forwardCount: Int?,
    val likeCount: Int?,
    val repliesCount: Int?,
    val sensitive: Boolean,
    val spoilerText: String,
    val mediaList: List<BlogMedia>,
    val poll: BlogPoll?,
)

@Dao
interface StatusContentDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri=:sourceUri")
    suspend fun queryBySourceUri(sourceUri: StatusProviderUri): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri IN (:sourceUriList) ORDER BY date DESC")
    suspend fun queryBySourceUriList(
        sourceUriList: List<StatusProviderUri>
    ): List<StatusContentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: StatusContentEntity)

    @Delete
    suspend fun delete(entity: StatusContentEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE id=:id")
    suspend fun deleteById(id: Long)
}
