package com.zhangke.utopia.activitypub.app.internal.db.status

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.utopia.activitypub.app.internal.db.converter.ActivityPubStatusEntityConverter
import com.zhangke.utopia.activitypub.app.internal.db.converter.ActivityPubStatusSourceTypeConverter
import com.zhangke.utopia.activitypub.app.internal.db.converter.FormalBaseUrlConverter
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.common.status.repo.db.converts.IdentityRoleConverter
import com.zhangke.utopia.common.status.repo.db.converts.StatusConverter
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status

private const val DB_NAME = "activity_pub_status.db"
private const val DB_VERSION = 1
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
    suspend fun query(role: IdentityRole, type: ActivityPubStatusSourceType, id: String): ActivityPubStatusTableEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id AND role = :role AND type = :type AND listId = :listId")
    suspend fun queryStatusInList(role: IdentityRole, type: ActivityPubStatusSourceType, listId: String, id: String): ActivityPubStatusTableEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryTimelineStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int,
    ): List<ActivityPubStatusTableEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type ORDER BY createTimestamp DESC LIMIT 1")
    suspend fun queryRecentStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType
    ): ActivityPubStatusTableEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type AND listId = :listId ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryListStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String,
        limit: Int
    ): List<ActivityPubStatusTableEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type AND listId = :listId ORDER BY createTimestamp DESC LIMIT 1")
    suspend fun queryRecentListStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String,
    ): ActivityPubStatusTableEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type AND createTimestamp <= :datetime ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryEarlierStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        limit: Int,
        datetime: Long,
    ): List<ActivityPubStatusTableEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type AND listId = :listId AND createTimestamp <= :datetime ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryEarlierListStatus(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String,
        limit: Int,
        datetime: Long,
    ): List<ActivityPubStatusTableEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ActivityPubStatusTableEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<ActivityPubStatusTableEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE role=:role AND type=:type")
    suspend fun delete(role: IdentityRole, type: ActivityPubStatusSourceType)

    @Query("DELETE FROM $TABLE_NAME WHERE role=:role AND type=:type AND listId=:listId")
    suspend fun deleteListStatus(role: IdentityRole, type: ActivityPubStatusSourceType, listId: String)

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

        private var instance: ActivityPubStatusDatabases? = null

        fun getInstance(context: Context): ActivityPubStatusDatabases {
            if (instance == null) {
                synchronized(ActivityPubStatusDatabases::class.java) {
                    if (instance == null) {
                        instance = createDatabase(context)
                    }
                }
            }
            return instance!!
        }

        private fun createDatabase(context: Context): ActivityPubStatusDatabases {
            return Room.databaseBuilder(
                context,
                ActivityPubStatusDatabases::class.java,
                DB_NAME
            ).build()
        }
    }
}
