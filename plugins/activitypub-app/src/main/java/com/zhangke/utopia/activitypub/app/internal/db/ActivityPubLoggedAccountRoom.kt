package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.*
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.utils.WebFinger
import kotlinx.coroutines.flow.Flow

private const val TABLE_NAME = "logged_accounts"

@Entity(tableName = TABLE_NAME)
data class ActivityPubLoggedAccountEntity(
    /**
     * it will container account id
     */
    @PrimaryKey val uri: String,
    /**
     * Not ActivityPub accountId, it`s WebFinger.
     */
    val userId: String,
    val webFinger: WebFinger,
    val platform: BlogPlatformEntity,
    val baseUrl: String,
    val name: String,
    val description: String?,
    val avatar: String?,
    val homepage: String?,
    val active: Boolean,
    val token: ActivityPubTokenEntity,
)

data class BlogPlatformEntity(
    val uri: String,
    val name: String,
    val description: String,
    val baseUrl: String,
    val protocol: String,
    val thumbnail: String?,
)

@Dao
interface ActivityPubLoggerAccountDao {

    @Query("SELECT * FROM $TABLE_NAME")
    fun queryAllFlow(): Flow<List<ActivityPubLoggedAccountEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE active=1")
    suspend fun queryActiveAccount(): ActivityPubLoggedAccountEntity?

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<ActivityPubLoggedAccountEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun queryByUri(uri: String): ActivityPubLoggedAccountEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE baseUrl=:baseUrl")
    suspend fun queryByBaseUrl(baseUrl: String): List<ActivityPubLoggedAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ActivityPubLoggedAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entries: List<ActivityPubLoggedAccountEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun deleteByUri(uri: String)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun nukeTable()
}
