package com.zhangke.utopia.common.status.repo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusType
import com.zhangke.utopia.status.uri.FormalUri

private const val TABLE_NAME = "status_content"

@Entity(tableName = TABLE_NAME)
data class StatusContentEntity(
    /**
     * 这个 ID 是获取到数据之后按照规则生成的，不是帖子原本的 ID。
     * 这个 ID 不会，也不应该暴露到repo之外，只用作内部。
     */
    @PrimaryKey val id: String,
    /**
     * 这个 ID 是服务端返回的，帖子实际上的 ID。
     */
    val statusIdOfPlatform: String,
    val sourceUri: FormalUri,
    val createTimestamp: Long,
    val status: Status,
    val isFirstStatus: Boolean,
)

@Dao
interface StatusContentDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<StatusContentEntity>

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

    @Query("SELECT * FROM $TABLE_NAME WHERE statusIdOfPlatform=:id")
    suspend fun queryByPlatformId(id: String): StatusContentEntity?

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

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri=:sourceUri ORDER BY createTimestamp DESC LIMIT 1")
    suspend fun queryRecentStatus(sourceUri: FormalUri): StatusContentEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri=:sourceUri ORDER BY createTimestamp ASC LIMIT 1")
    suspend fun queryEarliestStatus(sourceUri: FormalUri): StatusContentEntity?

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

    @Query("DELETE FROM $TABLE_NAME WHERE sourceUri=:uri")
    suspend fun deleteBySourceUri(uri: FormalUri)
}
