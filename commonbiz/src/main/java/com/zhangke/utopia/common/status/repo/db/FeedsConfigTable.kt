package com.zhangke.utopia.common.status.repo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow

private const val TABLE_NAME = "feeds_configs"

@Entity(tableName = TABLE_NAME)
data class FeedsConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val name: String,
    val sourceUriList: List<FormalUri>,
    val lastReadStatusId: String?,
)

@Dao
interface FeedsConfigDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAllFeedsConfig(): List<FeedsConfigEntity>

    @Query("SELECT * FROM $TABLE_NAME")
    fun getAllFeedsConfigFlow(): Flow<List<FeedsConfigEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE id=:id")
    suspend fun queryById(id: Long): FeedsConfigEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE name=:name")
    suspend fun queryByName(name: String): List<FeedsConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: FeedsConfigEntity)

    @Query("UPDATE $TABLE_NAME SET lastReadStatusId=null")
    suspend fun clearAllLastReadStatusId()

    @Query("UPDATE $TABLE_NAME SET sourceUriList=:sourceUriList WHERE id=:feedsId")
    suspend fun updateSourceList(
        feedsId: Long,
        sourceUriList: List<FormalUri>,
    )

    @Query("UPDATE $TABLE_NAME SET name=:name WHERE id=:feedsId")
    suspend fun updateFeedsName(
        feedsId: Long,
        name: String,
    )

    @Delete
    suspend fun delete(entity: FeedsConfigEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE id=:id")
    suspend fun deleteById(id: Long)
}
