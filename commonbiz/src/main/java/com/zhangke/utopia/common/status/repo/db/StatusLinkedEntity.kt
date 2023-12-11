package com.zhangke.utopia.common.status.repo.db

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

private const val TABLE_NAME = "status_linked"

@Entity(tableName = TABLE_NAME)
data class StatusLinkedEntity(
    @PrimaryKey val id: String,
    val nextId: String,
)

@Dao
interface StatusLinkedDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    suspend fun queryById(id: String): StatusLinkedEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(entity: StatusLinkedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceList(list: List<StatusLinkedEntity>)

    @Query("DELETE $TABLE_NAME WHERE id = :id")
    suspend fun deleteById(id: String)
}
