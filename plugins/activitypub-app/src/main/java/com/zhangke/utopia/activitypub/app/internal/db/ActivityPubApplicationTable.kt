package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.zhangke.framework.network.FormalBaseUrl

private const val TABLE_NAME = "application_list"

@Entity(tableName = TABLE_NAME)
data class ActivityPubApplicationEntity(
    @PrimaryKey val baseUrl: FormalBaseUrl,
    val id: String,
    val name: String,
    val website: String,
    val redirectUri: String,
    val clientId: String,
    val clientSecret: String,
    val vapidKey: String,
)

@Dao
interface ActivityPubApplicationsDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE baseUrl=:baseUrl")
    suspend fun queryByBaseUrl(baseUrl: FormalBaseUrl): ActivityPubApplicationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(applicationEntity: ActivityPubApplicationEntity)

    @Delete
    suspend fun delete(applicationEntity: ActivityPubApplicationEntity)
}
