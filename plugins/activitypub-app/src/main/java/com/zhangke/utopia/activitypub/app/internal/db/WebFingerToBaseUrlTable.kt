package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.zhangke.framework.utils.WebFinger

private const val TABLE_NAME = "webFingerToBaseUrl"

@Entity(tableName = TABLE_NAME)
data class WebFingerToBaseUrlEntity(
    @PrimaryKey val webFinger: WebFinger,
    val baseUrl: String,
)

@Dao
interface WebFingerToBaseUrlDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE webFinger = :webFinger")
    suspend fun queryBaseUrl(webFinger: WebFinger): String?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WebFingerToBaseUrlEntity)

    @Delete
    suspend fun delete(entity: WebFingerToBaseUrlEntity)
}
