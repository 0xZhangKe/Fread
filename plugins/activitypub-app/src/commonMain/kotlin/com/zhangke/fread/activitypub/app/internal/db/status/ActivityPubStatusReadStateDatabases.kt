package com.zhangke.fread.activitypub.app.internal.db.status

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubStatusSourceTypeConverter
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.common.status.repo.db.converts.IdentityRoleConverter
import com.zhangke.fread.status.model.IdentityRole

private const val DB_VERSION = 1
private const val TABLE_NAME = "activity_pub_status_read_state"

@Entity(tableName = TABLE_NAME, primaryKeys = ["role", "type", "listId"])
data class ActivityPubStatusReadStateEntity(
    val role: IdentityRole,
    val type: ActivityPubStatusSourceType,
    val listId: String,
    val latestReadId: String?,
)

@Dao
interface ActivityPubStatusReadStateDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type")
    suspend fun query(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
    ): ActivityPubStatusReadStateEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE role = :role AND type = :type AND listId = :listId")
    suspend fun queryList(
        role: IdentityRole,
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
    IdentityRoleConverter::class,
)
@Database(
    entities = [ActivityPubStatusReadStateEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class ActivityPubStatusReadStateDatabases : RoomDatabase() {

    abstract fun getDao(): ActivityPubStatusReadStateDao

    companion object {
        internal const val DB_NAME = "activity_pub_status_read_state.db"
    }
}
