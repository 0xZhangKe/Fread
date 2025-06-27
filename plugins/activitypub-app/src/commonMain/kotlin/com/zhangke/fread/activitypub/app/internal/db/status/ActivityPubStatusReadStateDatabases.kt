package com.zhangke.fread.activitypub.app.internal.db.status

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubStatusSourceTypeConverter
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.common.db.converts.PlatformLocatorConverter
import com.zhangke.fread.status.model.PlatformLocator

private const val DB_VERSION = 1
private const val TABLE_NAME = "activity_pub_status_read_state"

@Entity(tableName = TABLE_NAME, primaryKeys = ["locator", "type", "listId"])
data class ActivityPubStatusReadStateEntity(
    val locator: PlatformLocator,
    val type: ActivityPubStatusSourceType,
    val listId: String,
    val latestReadId: String?,
)

@Dao
interface ActivityPubStatusReadStateDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE locator = :locator AND type = :type")
    suspend fun query(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
    ): ActivityPubStatusReadStateEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE locator = :locator AND type = :type AND listId = :listId")
    suspend fun queryList(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String,
    ): ActivityPubStatusReadStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(entity: ActivityPubStatusReadStateEntity)

    @Delete
    suspend fun delete(entity: ActivityPubStatusReadStateEntity)
}

@TypeConverters(
    ActivityPubStatusSourceTypeConverter::class,
    PlatformLocatorConverter::class,
)
@Database(
    entities = [ActivityPubStatusReadStateEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
@ConstructedBy(ActivityPubStatusReadStateDatabasesConstructor::class)
abstract class ActivityPubStatusReadStateDatabases : RoomDatabase() {

    abstract fun getDao(): ActivityPubStatusReadStateDao

    companion object {
        internal const val DB_NAME = "activity_pub_status_read_state_1.db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ActivityPubStatusReadStateDatabasesConstructor :
    RoomDatabaseConstructor<ActivityPubStatusReadStateDatabases> {
    override fun initialize(): ActivityPubStatusReadStateDatabases
}
