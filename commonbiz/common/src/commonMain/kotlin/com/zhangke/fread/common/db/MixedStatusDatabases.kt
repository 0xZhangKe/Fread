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
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.zhangke.fread.common.db.converts.FormalUriConverter
import com.zhangke.fread.common.db.converts.StatusConverter
import com.zhangke.fread.common.db.converts.StatusUiStateConverter
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.Flow

private const val DB_VERSION = 2
private const val TABLE_NAME = "mixed_status"

@Entity(tableName = TABLE_NAME, primaryKeys = ["sourceUri", "statusId"])
data class MixedStatusEntity(
    val statusId: String,
    val sourceUri: FormalUri,
    val status: StatusUiState,
    val createAt: Long,
    val cursor: String?,
)

@Dao
interface MixedStatusDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri IN (:sourceUris) ORDER BY createAt DESC")
    fun queryFlow(sourceUris: List<FormalUri>): Flow<List<MixedStatusEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE statusId = :statusId")
    suspend fun queryByStatusId(statusId: String): List<MixedStatusEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri IN (:sourceUris) ORDER BY createAt DESC")
    suspend fun queryAll(sourceUris: List<FormalUri>): List<MixedStatusEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE sourceUri = :sourceUri ORDER BY createAt DESC")
    suspend fun query(sourceUri: FormalUri): List<MixedStatusEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: List<MixedStatusEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE sourceUri IN (:sourceUris)")
    suspend fun deleteStatusOfSources(sourceUris: List<FormalUri>)

    @Query("DELETE FROM $TABLE_NAME WHERE sourceUri = :sourceUri")
    suspend fun deleteStatusOfSource(sourceUri: FormalUri)
}

@TypeConverters(
    StatusConverter::class,
    StatusUiStateConverter::class,
    FormalUriConverter::class,
)
@Database(
    entities = [MixedStatusEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
@ConstructedBy(MixedStatusDatabasesConstructor::class)
abstract class MixedStatusDatabases : RoomDatabase() {

    abstract fun mixedStatusDao(): MixedStatusDao

    companion object {

        const val DB_NAME = "mixed_status_1.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {

            override fun migrate(connection: SQLiteConnection) {
                connection.execSQL("DELETE FROM $TABLE_NAME")
            }
        }
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object MixedStatusDatabasesConstructor : RoomDatabaseConstructor<MixedStatusDatabases> {
    override fun initialize(): MixedStatusDatabases
}
