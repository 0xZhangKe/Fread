package com.zhangke.fread.activitypub.app.internal.db.status

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
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubStatusEntityConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubStatusSourceTypeConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.FormalBaseUrlConverter
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.common.db.converts.PlatformLocatorConverter
import com.zhangke.fread.common.db.converts.StatusConverter
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.status.model.Status

private const val DB_VERSION = 1
private const val TABLE_NAME = "activity_pub_status"

@Entity(tableName = TABLE_NAME, primaryKeys = ["id", "locator", "type", "listId"])
data class ActivityPubStatusTableEntity(
    // Status id
    val id: String,
    val locator: PlatformLocator,
    val type: ActivityPubStatusSourceType,
    val listId: String,
    val status: Status,
    val createTimestamp: Long,
)

@Dao
interface ActivityPubStatusDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id AND locator = :locator AND type = :type")
    suspend fun query(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        id: String
    ): ActivityPubStatusTableEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id AND locator = :locator AND type = :type AND listId = :listId ORDER BY createTimestamp DESC")
    suspend fun queryStatusInList(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String,
        id: String
    ): ActivityPubStatusTableEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE locator = :locator AND type = :type ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryTimelineStatus(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        limit: Int,
    ): List<ActivityPubStatusTableEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE locator = :locator AND type = :type AND listId = :listId ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryListStatus(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String,
        limit: Int
    ): List<ActivityPubStatusTableEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ActivityPubStatusTableEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<ActivityPubStatusTableEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE locator=:locator AND type=:type")
    suspend fun delete(locator: PlatformLocator, type: ActivityPubStatusSourceType)

    @Query("DELETE FROM $TABLE_NAME WHERE locator=:locator AND type=:type AND listId=:listId")
    suspend fun deleteListStatus(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String
    )

    @Query("DELETE FROM $TABLE_NAME WHERE id=:id")
    suspend fun delete(id: String)
}

@TypeConverters(
    FormalBaseUrlConverter::class,
    ActivityPubStatusSourceTypeConverter::class,
    ActivityPubStatusEntityConverter::class,
    StatusConverter::class,
    PlatformLocatorConverter::class,
)
@Database(
    entities = [ActivityPubStatusTableEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
@ConstructedBy(ActivityPubStatusDatabasesConstructor::class)
abstract class ActivityPubStatusDatabases : RoomDatabase() {

    abstract fun getDao(): ActivityPubStatusDao

    companion object {
        const val DB_NAME = "activity_pub_status_1.db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ActivityPubStatusDatabasesConstructor :
    RoomDatabaseConstructor<ActivityPubStatusDatabases> {
    override fun initialize(): ActivityPubStatusDatabases
}

