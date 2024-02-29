package com.zhangke.utopia.common.status.repo.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentType
import com.zhangke.utopia.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow

private const val TABLE_NAME = "content_configs"

@Entity(tableName = TABLE_NAME)
data class ContentConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val order: Int,
    val name: String,
    val type: ContentType,
    val sourceUriList: List<FormalUri>?,
    val baseUrl: FormalBaseUrl?,
    val lastReadStatusId: String?,
    val showingTabList: List<ContentConfig.ActivityPubContent.TabConfig>,
    val hideTabList: List<ContentConfig.ActivityPubContent.TabConfig>,
)

@Dao
interface ContentConfigDao {

    @Query("SELECT * FROM $TABLE_NAME ORDER BY `order` ASC")
    suspend fun queryAllContentConfig(): List<ContentConfigEntity>

    @Query("SELECT * FROM $TABLE_NAME ORDER BY `order` ASC")
    fun queryAllContentConfigFlow(): Flow<List<ContentConfigEntity>>

    @Query("UPDATE $TABLE_NAME SET lastReadStatusId=null")
    suspend fun clearAllLastReadStatusId()

    @Query("SELECT * FROM $TABLE_NAME WHERE name=:name")
    suspend fun queryByName(name: String): ContentConfigEntity?

    @Query("UPDATE $TABLE_NAME SET sourceUriList=:sourceUriList WHERE id=:id")
    suspend fun updateSourceList(id: Long, sourceUriList: List<FormalUri>)

    @Query("UPDATE $TABLE_NAME SET lastReadStatusId=:latestStatusId WHERE id=:id")
    suspend fun updateLatestStatusId(id: Long, latestStatusId: String?)

    @Query("UPDATE $TABLE_NAME SET name=:name WHERE id=:id")
    suspend fun updateName(id: Long, name: String)

    @Query("SELECT * FROM $TABLE_NAME WHERE id=:id")
    suspend fun queryById(id: Long): ContentConfigEntity?

    @Query("SELECT MAX(`order`) FROM $TABLE_NAME")
    suspend fun queryMaxOrder(): Int?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ContentConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(entities: List<ContentConfigEntity>)

    @Delete
    suspend fun delete(entity: ContentConfigEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE id=:id")
    suspend fun deleteById(id: Long)
}
