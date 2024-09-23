package com.zhangke.fread.common.status.repo.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.common.status.repo.db.converts.ContentTabConverter
import com.zhangke.fread.common.status.repo.db.converts.ContentTypeConverter
import com.zhangke.fread.common.status.repo.db.converts.FormalBaseUrlConverter
import com.zhangke.fread.common.status.repo.db.converts.StatusProviderUriConverter
import com.zhangke.fread.common.status.repo.db.converts.StatusProviderUriListConverter
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.ContentType
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow

private const val DB_VERSION = 1
private const val TABLE_NAME = "content_configs"

@TypeConverters(
    ContentTabConverter::class,
)
@Entity(tableName = TABLE_NAME)
data class ContentConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    val order: Int,
    val name: String,
    val type: ContentType,
    val sourceUriList: List<FormalUri>?,
    val baseUrl: FormalBaseUrl?,
    val showingTabList: List<ContentConfig.ActivityPubContent.ContentTab>,
    val hiddenTabList: List<ContentConfig.ActivityPubContent.ContentTab>,
)

@Dao
interface ContentConfigDao {

    @Query("SELECT * FROM $TABLE_NAME ORDER BY `order` ASC")
    suspend fun queryAllContentConfig(): List<ContentConfigEntity>

    @Query("SELECT * FROM $TABLE_NAME ORDER BY `order` ASC")
    fun queryAllContentConfigFlow(): Flow<List<ContentConfigEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE id=:id")
    fun getContentConfigFlow(id: Long): Flow<ContentConfigEntity?>

    @Query("SELECT * FROM $TABLE_NAME WHERE name=:name")
    suspend fun queryByName(name: String): ContentConfigEntity?

    @Query("UPDATE $TABLE_NAME SET sourceUriList=:sourceUriList WHERE id=:id")
    suspend fun updateSourceList(id: Long, sourceUriList: List<FormalUri>)

    @Query("UPDATE $TABLE_NAME SET name=:name WHERE id=:id")
    suspend fun updateName(id: Long, name: String)

    @Query("SELECT * FROM $TABLE_NAME WHERE id=:id")
    suspend fun queryById(id: Long): ContentConfigEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE id=:id")
    fun queryFlowById(id: Long): Flow<ContentConfigEntity?>

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

@TypeConverters(
    ContentTabConverter::class,
    ContentTypeConverter::class,
    StatusProviderUriConverter::class,
    StatusProviderUriListConverter::class,
    FormalBaseUrlConverter::class,
)
@Database(
    entities = [ContentConfigEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class ContentConfigDatabases : RoomDatabase() {

    abstract fun getContentConfigDao(): ContentConfigDao

    companion object {
        const val DB_NAME = "ContentConfig.db"
    }
}
