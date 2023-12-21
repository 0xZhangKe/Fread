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
import com.zhangke.utopia.status.uri.FormalUri

private const val TABLE_NAME = "status_content"

@Entity(tableName = TABLE_NAME)
data class StatusContentEntity(
    @PrimaryKey val id: String,
    val nextStatusId: String?,
    val authorUri: FormalUri,
    val authorWebFinger: WebFinger,
    val authorName: String,
    val authorDescription: String,
    val authorAvatar: String?,
    val sourceUri: FormalUri,
    val type: StatusType,
    val statusIdOfPlatform: String,
    val title: String?,
    val content: String,
    val createTimestamp: Long,
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
    suspend fun queryBySource(sourceUri: FormalUri): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri=:sourceUri LIMIT :limit")
    suspend fun queryBySource(sourceUri: FormalUri, limit: Int): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri IN (:sourceUriList) ORDER BY createTimestamp DESC")
    suspend fun queryBySource(
        sourceUriList: List<FormalUri>,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE id IN (:idList) ORDER BY createTimestamp DESC")
    suspend fun query(
        idList: List<String>,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri IN (:sourceUriList) ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryBySource(
        sourceUriList: List<FormalUri>,
        limit: Int,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE id=:id")
    suspend fun query(id: String): StatusContentEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE createTimestamp<=:createTimestamp AND sourceUri= :sourceUri ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryPrevious(
        sourceUri: FormalUri,
        createTimestamp: Long,
        limit: Int,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE createTimestamp<=:createTimestamp AND sourceUri IN (:sourceUriList) ORDER BY createTimestamp DESC")
    suspend fun queryPrevious(
        sourceUriList: List<FormalUri>,
        createTimestamp: Long,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE createTimestamp<=:createTimestamp AND sourceUri IN (:sourceUriList) ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryPrevious(
        sourceUriList: List<FormalUri>,
        createTimestamp: Long,
        limit: Int,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE createTimestamp<=:createTimestamp AND sourceUri=:sourceUri")
    suspend fun queryRecentPrevious(
        sourceUri: FormalUri,
        createTimestamp: Long,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE createTimestamp>=:createTimestamp AND sourceUri=:sourceUri ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryNewer(
        sourceUri: FormalUri,
        createTimestamp: Long,
        limit: Int,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE createTimestamp>=:createTimestamp AND sourceUri=:sourceUri")
    suspend fun queryRecentNewer(
        sourceUri: FormalUri,
        createTimestamp: Long,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE createTimestamp>=:createTimestamp AND sourceUri IN (:sourceUriList) ORDER BY createTimestamp DESC")
    suspend fun queryNewer(
        sourceUriList: List<FormalUri>,
        createTimestamp: Long,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE createTimestamp>=:createTimestamp AND sourceUri IN (:sourceUriList) ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryNewer(
        sourceUriList: List<FormalUri>,
        createTimestamp: Long,
        limit: Int,
    ): List<StatusContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri=:sourceUri ORDER BY createTimestamp LIMIT 1")
    suspend fun queryFirst(sourceUri: FormalUri): StatusContentEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri=:sourceUri ORDER BY createTimestamp DESC LIMIT 1")
    suspend fun queryLatest(sourceUri: FormalUri): StatusContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: StatusContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entityList: List<StatusContentEntity>)

    @Delete
    suspend fun delete(entity: StatusContentEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE id=:id")
    suspend fun deleteById(id: Long)
}
