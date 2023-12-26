package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.zhangke.framework.utils.WebFinger

private const val TABLE_NAME = "web_finger_baseurl_to_id"

@Entity(tableName = TABLE_NAME, primaryKeys = ["webFinger", "baseUrl"])
data class WebFingerBaseurlToIdEntity(
    val webFinger: WebFinger,
    val baseUrl: String,
    val userId: String,
)

@Dao
interface WebFingerBaseurlToIdDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE webFinger = :webFinger AND baseUrl = :baseUrl")
    suspend fun queryUserId(webFinger: WebFinger, baseUrl: String): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WebFingerBaseurlToIdEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE webFinger = :webFinger AND baseUrl = :baseUrl")
    suspend fun delete(webFinger: WebFinger, baseUrl: String)
}
