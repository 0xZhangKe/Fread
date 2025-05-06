package com.zhangke.fread.common.db

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.zhangke.fread.common.db.converts.FreadContentConverter
import com.zhangke.fread.status.model.FreadContent
import kotlinx.coroutines.flow.Flow

private const val DB_VERSION = 1
private const val TABLE_NAME = "fread_content"

@Entity(tableName = TABLE_NAME)
data class FreadContentEntity(
    @PrimaryKey val id: String,
    val content: FreadContent,
)

@Dao
interface FreadContentDao {

    @Query("SELECT * FROM $TABLE_NAME")
    fun queryAllFlow(): Flow<List<FreadContentEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun queryFlow(id: String): Flow<FreadContentEntity?>

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<FreadContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    suspend fun query(id: String): FreadContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(content: FreadContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<FreadContentEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    suspend fun delete(id: String)
}

@TypeConverters(
    FreadContentConverter::class
)
@Database(
    entities = [FreadContentEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
@ConstructedBy(FreadContentDatabaseConstructor::class)
abstract class FreadContentDatabase : RoomDatabase() {

    abstract fun contentDao(): FreadContentDao

    companion object {
        const val DB_NAME = "fread_content.db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object FreadContentDatabaseConstructor : RoomDatabaseConstructor<FreadContentDatabase> {
    override fun initialize(): FreadContentDatabase
}
