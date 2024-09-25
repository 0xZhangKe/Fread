package com.zhangke.fread.activitypub.app.internal.db.status

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubStatusEntityConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubStatusSourceTypeConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.FormalBaseUrlConverter
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.common.status.repo.db.converts.IdentityRoleConverter
import com.zhangke.fread.common.status.repo.db.converts.StatusConverter
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status

private const val DB_VERSION = 2
private const val TABLE_NAME = "activity_pub_status"

@Entity(tableName = TABLE_NAME, primaryKeys = ["id", "role", "type", "listId"])
data class ActivityPubStatusTableEntity(
    // Status id
    val id: String,
    val role: IdentityRole,
    val type: ActivityPubStatusSourceType,
    val listId: String,
    val status: Status,
    val createTimestamp: Long,
)

@Dao
interface ActivityPubStatusDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id AND role = :role AND type = :type")
    suspend fun query(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        id: String
    ): ActivityPubStatusTableEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id AND role = :role AND type = :type AND listId = :listId ORDER BY createTimestamp DESC")
    suspend fun queryStatusInList(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String,
        id: String
    ): ActivityPubStatusTableEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryTimelineStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int,
    ): List<ActivityPubStatusTableEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type AND listId = :listId ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryListStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String,
        limit: Int
    ): List<ActivityPubStatusTableEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ActivityPubStatusTableEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<ActivityPubStatusTableEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE role=:role AND type=:type")
    suspend fun delete(role: IdentityRole, type: ActivityPubStatusSourceType)

    @Query("DELETE FROM $TABLE_NAME WHERE role=:role AND type=:type AND listId=:listId")
    suspend fun deleteListStatus(
        role: IdentityRole,
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
    IdentityRoleConverter::class,
)
@Database(
    entities = [ActivityPubStatusTableEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class ActivityPubStatusDatabases : RoomDatabase() {

    abstract fun getDao(): ActivityPubStatusDao

    companion object {
        const val DB_NAME = "activity_pub_status.db"
    }

    internal class Status1to2Migration : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("DELETE FROM $TABLE_NAME")
        }
    }
}


