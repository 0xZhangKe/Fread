package com.zhangke.fread.bluesky.internal.db

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.db.converter.BlueskyLoggedAccountConverter
import kotlinx.coroutines.flow.Flow

private const val TABLE_NAME = "logged_accounts"
private const val DB_VERSION = 1

@Entity(tableName = TABLE_NAME)
data class BlueskyLoggedAccountEntity(
    @PrimaryKey val uri: String,
    val account: BlueskyLoggedAccount,
    val addedTimestamp: Long,
)

@Dao
interface BlueskyLoggedAccountDao {

    @Query("SELECT * FROM $TABLE_NAME ORDER BY addedTimestamp")
    fun queryAllFlow(): Flow<List<BlueskyLoggedAccountEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun queryByUri(uri: String): BlueskyLoggedAccountEntity?

    @Query("SELECT * FROM $TABLE_NAME ORDER BY addedTimestamp")
    suspend fun queryAll(): List<BlueskyLoggedAccountEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: BlueskyLoggedAccountEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun deleteByUri(uri: String)
}

@TypeConverters(BlueskyLoggedAccountConverter::class)
@Database(
    entities = [
        BlueskyLoggedAccountEntity::class,
    ],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class BlueskyLoggedAccountDatabase : RoomDatabase() {

    abstract fun getDao(): BlueskyLoggedAccountDao

    companion object {

        internal const val DB_NAME = "bluesky_logged_accounts.db"
    }
}
