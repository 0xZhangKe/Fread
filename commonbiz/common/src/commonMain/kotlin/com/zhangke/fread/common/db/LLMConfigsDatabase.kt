package com.zhangke.fread.common.db

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.zhangke.fread.common.ai.model.LLMModelConfig
import com.zhangke.fread.common.db.converts.LLMModelConfigsConverter
import kotlinx.coroutines.flow.Flow

private const val DB_VERSION = 1
private const val TABLE_NAME = "llm_providers"

@Entity(
    tableName = TABLE_NAME,
    primaryKeys = ["providerId", "versionName"],
)
data class LLMModelConfigEntity(
    val providerId: String,
    val versionName: String,
    val provider: LLMModelConfig,
)

@Dao
interface LLMModelConfigsDao {

    @Query("SELECT * FROM $TABLE_NAME")
    fun queryAllFlow(): Flow<List<LLMModelConfigEntity>>

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<LLMModelConfigEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE providerId = :providerId AND versionName = :versionName")
    suspend fun query(providerId: String, versionName: String): LLMModelConfigEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(provider: LLMModelConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<LLMModelConfigEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE providerId = :providerId AND versionName = :versionName")
    suspend fun delete(providerId: String, versionName: String)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteAll()
}

@TypeConverters(
    LLMModelConfigsConverter::class,
)
@Database(
    entities = [LLMModelConfigEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
@ConstructedBy(LLMModelConfigsDatabaseConstructor::class)
abstract class LLMModelConfigsDatabase : RoomDatabase() {

    abstract fun providerDao(): LLMModelConfigsDao

    companion object {

        const val DB_NAME = "llm_model_configs.db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LLMModelConfigsDatabaseConstructor :
    RoomDatabaseConstructor<LLMModelConfigsDatabase> {
    override fun initialize(): LLMModelConfigsDatabase
}
