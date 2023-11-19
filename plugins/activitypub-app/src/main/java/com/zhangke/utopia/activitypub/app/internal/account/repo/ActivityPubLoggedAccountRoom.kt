package com.zhangke.utopia.activitypub.app.internal.account.repo

import androidx.room.*
import com.zhangke.activitypub.entry.ActivityPubTokenEntity
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.account.entities.BlogPlatformEntity
import com.zhangke.utopia.status.platform.BlogPlatform

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
    val host: String,
    val name: String,
    val description: String?,
    val avatar: String?,
    val homepage: String?,
    val active: Boolean,
    val token: ActivityPubTokenEntity,
)

@Dao
interface ActivityPubLoggerAccountDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE active='true'")
    suspend fun querySelectedAccount(): ActivityPubLoggedAccountEntity?

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<ActivityPubLoggedAccountEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun queryByUri(uri: String): ActivityPubLoggedAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ActivityPubLoggedAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entries: List<ActivityPubLoggedAccountEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun deleteByUri(uri: String)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun nukeTable()
}
