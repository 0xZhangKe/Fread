package com.zhangke.fread.activitypub.app.internal.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.zhangke.activitypub.entities.ActivityPubTokenEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.status.model.Emoji
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

private const val TABLE_NAME = "logged_accounts"

@Entity(tableName = TABLE_NAME)
data class ActivityPubLoggedAccountEntity(
    @PrimaryKey val uri: String,
    val userId: String,
    val webFinger: WebFinger,
    val platform: BlogPlatformEntity,
    val baseUrl: FormalBaseUrl,
    val name: String,
    val description: String?,
    val avatar: String?,
    val url: String,
    val token: ActivityPubTokenEntity,
    val emojis: List<Emoji>,
    val addedTimestamp: Long,
) {

    @Serializable
    data class BlogPlatformEntity(
        val uri: String,
        val name: String,
        val description: String,
        val baseUrl: FormalBaseUrl,
        val thumbnail: String?,
    )
}

@Dao
interface ActivityPubLoggerAccountDao {

    @Query("SELECT * FROM $TABLE_NAME ORDER BY addedTimestamp")
    fun queryAllFlow(): Flow<List<ActivityPubLoggedAccountEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    fun observeAccount(uri: String): Flow<ActivityPubLoggedAccountEntity?>

    @Query("SELECT * FROM $TABLE_NAME ORDER BY addedTimestamp")
    suspend fun queryAll(): List<ActivityPubLoggedAccountEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE userId=:userId")
    suspend fun queryById(userId: String): ActivityPubLoggedAccountEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun queryByUri(uri: String): ActivityPubLoggedAccountEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE baseUrl=:baseUrl ORDER BY addedTimestamp")
    suspend fun queryByBaseUrl(baseUrl: FormalBaseUrl): List<ActivityPubLoggedAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ActivityPubLoggedAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entries: List<ActivityPubLoggedAccountEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun deleteByUri(uri: String)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun nukeTable()
}
