package com.zhangke.utopia.common.status.repo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.zhangke.utopia.status.uri.StatusProviderUri

private const val TABLE_NAME = "status_source"

@Entity(tableName = TABLE_NAME)
data class StatusSourceEntity(
    @PrimaryKey val uri: StatusProviderUri,
    val name: String,
    val description: String,
    val thumbnail: String?,
)

@Dao
interface StatusSourceDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAllSource(): List<StatusSourceEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun queryByUri(uri: StatusProviderUri): StatusSourceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: StatusSourceEntity)

    @Delete
    suspend fun delete(entity: StatusSourceEntity)
}
