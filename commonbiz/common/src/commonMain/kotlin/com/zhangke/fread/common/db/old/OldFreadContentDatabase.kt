package com.zhangke.fread.common.db.old

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
data class OldFreadContentEntity(
    @PrimaryKey val id: String,
    val content: FreadContent,
)

@Dao
interface OldFreadContentDao {

    @Query("SELECT * FROM $TABLE_NAME")
    fun queryAllFlow(): Flow<List<OldFreadContentEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    fun queryFlow(id: String): Flow<OldFreadContentEntity?>

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<OldFreadContentEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    suspend fun query(id: String): OldFreadContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(content: OldFreadContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<OldFreadContentEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE id = :id")
    suspend fun delete(id: String)
}

@TypeConverters(
    FreadContentConverter::class
)
@Database(
    entities = [OldFreadContentEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
@ConstructedBy(OldFreadContentDatabaseConstructor::class)
abstract class OldFreadContentDatabase : RoomDatabase() {

    abstract fun contentDao(): OldFreadContentDao

    companion object {
        const val DB_NAME = "fread_content.db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object OldFreadContentDatabaseConstructor : RoomDatabaseConstructor<OldFreadContentDatabase> {
    override fun initialize(): OldFreadContentDatabase
}
