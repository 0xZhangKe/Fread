package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.zhangke.activitypub.entities.ActivityPubInstanceEntity
import com.zhangke.framework.network.FormalBaseUrl

private const val TABLE_NAME = "platform_info"

@Entity(tableName = TABLE_NAME)
data class ActivityPubInstanceInfoEntity(
    @PrimaryKey val uri: String,
    val baseUrl: FormalBaseUrl,
    val instanceEntity: ActivityPubInstanceEntity,
)

@Dao
interface ActivityPubPlatformDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<ActivityPubInstanceInfoEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun queryByUri(uri: String): ActivityPubInstanceInfoEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE baseUrl=:baseUrl")
    suspend fun queryByBaseUrl(baseUrl: FormalBaseUrl): ActivityPubInstanceInfoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ActivityPubInstanceInfoEntity)

    @Delete
    suspend fun deleteByUri(entity: ActivityPubInstanceInfoEntity)
}
