package com.zhangke.utopia.activitypubapp.account.repo

import androidx.room.*
import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.utopia.status.platform.UtopiaPlatform

private const val TABLE_NAME = "logged_users"

@Entity(tableName = TABLE_NAME)
data class ActivityPubUserEntity(
    /**
     * it will container user id
     */
    @PrimaryKey val uri: String,
    /**
     * Not ActivityPub userId, it`s WebFinger.
     */
    val id: String,
    val platform: UtopiaPlatform,
    val host: String,
    val name: String,
    val description: String?,
    val avatar: String?,
    val homepage: String?,
    val active: Boolean,
    val token: ActivityPubTokenEntity,
)

@Dao
interface ActivityPubUserDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE active='true'")
    suspend fun querySelectedUser(): ActivityPubUserEntity?

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<ActivityPubUserEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun queryByUri(uri: String): ActivityPubUserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ActivityPubUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entries: List<ActivityPubUserEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun deleteByUri(uri: String)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun nukeTable()
}
