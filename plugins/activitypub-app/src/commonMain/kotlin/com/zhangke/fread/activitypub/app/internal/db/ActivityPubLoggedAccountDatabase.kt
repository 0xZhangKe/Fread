package com.zhangke.fread.activitypub.app.internal.db

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
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubLoggedAccountConverter
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import kotlinx.coroutines.flow.Flow

private const val DB_VERSION = 1
private const val TABLE_NAME = "logged_accounts"

@Entity(tableName = TABLE_NAME)
data class ActivityPubLoggedAccountEntity(
    @PrimaryKey val uri: String,
    val account: ActivityPubLoggedAccount,
    val addedTimestamp: Long,
)

@Dao
interface ActivityPubLoggedAccountDao {

    @Query("SELECT * FROM $TABLE_NAME ORDER BY addedTimestamp")
    fun queryAllFlow(): Flow<List<ActivityPubLoggedAccountEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    fun observeAccount(uri: String): Flow<ActivityPubLoggedAccountEntity?>

    @Query("SELECT * FROM $TABLE_NAME ORDER BY addedTimestamp")
    suspend fun queryAll(): List<ActivityPubLoggedAccountEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun queryByUri(uri: String): ActivityPubLoggedAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ActivityPubLoggedAccountEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entries: List<ActivityPubLoggedAccountEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE uri=:uri")
    suspend fun deleteByUri(uri: String)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun nukeTable()
}

@TypeConverters(ActivityPubLoggedAccountConverter::class)
@Database(
    entities = [
        ActivityPubLoggedAccountEntity::class,
    ],
    version = DB_VERSION,
    exportSchema = false,
)
@ConstructedBy(ActivityPubLoggedAccountDatabaseConstructor::class)
abstract class ActivityPubLoggedAccountDatabase : RoomDatabase() {

    abstract fun getDao(): ActivityPubLoggedAccountDao

    companion object {
        internal const val DB_NAME = "ActivityPubLoggedAccount.db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ActivityPubLoggedAccountDatabaseConstructor :
    RoomDatabaseConstructor<ActivityPubLoggedAccountDatabase> {
    override fun initialize(): ActivityPubLoggedAccountDatabase
}
