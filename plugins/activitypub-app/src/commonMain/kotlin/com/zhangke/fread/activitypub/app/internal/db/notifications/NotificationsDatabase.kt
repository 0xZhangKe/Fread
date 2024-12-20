package com.zhangke.fread.activitypub.app.internal.db.notifications

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
import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.zhangke.activitypub.entities.ActivityPubAccountEntity
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubAccountEntityConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.FormalUriConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.RelationshipSeveranceEventConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.StatusNotificationTypeConverter
import com.zhangke.fread.activitypub.app.internal.model.RelationshipSeveranceEvent
import com.zhangke.fread.activitypub.app.internal.model.StatusNotificationType
import com.zhangke.fread.common.status.repo.db.converts.StatusConverter
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.uri.FormalUri

private const val DB_VERSION = 2


private const val TABLE_NAME = "notifications"

@Entity(tableName = TABLE_NAME)
data class NotificationsEntity(
    @PrimaryKey val notificationId: String,
    val type: StatusNotificationType,
    val accountOwnershipUri: FormalUri,
    val createTimestamp: Long,
    val account: ActivityPubAccountEntity,
    val status: Status?,
    val relationshipSeveranceEvent: RelationshipSeveranceEvent?,
)

@Dao
interface NotificationsDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE notificationId = :notificationId")
    suspend fun query(notificationId: String): NotificationsEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE accountOwnershipUri = :accountOwnershipUri ORDER BY createTimestamp DESC")
    suspend fun query(accountOwnershipUri: FormalUri): List<NotificationsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: NotificationsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<NotificationsEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE notificationId=:notificationId")
    suspend fun delete(notificationId: String)

    @Query("DELETE FROM $TABLE_NAME WHERE accountOwnershipUri=:accountOwnershipUri")
    suspend fun deleteByAccountUri(accountOwnershipUri: FormalUri)
}

@TypeConverters(
    RelationshipSeveranceEventConverter::class,
    StatusNotificationTypeConverter::class,
    ActivityPubAccountEntityConverter::class,
    FormalUriConverter::class,
    StatusConverter::class,
)
@Database(entities = [NotificationsEntity::class], version = DB_VERSION, exportSchema = false)
@ConstructedBy(NotificationsDatabaseConstructor::class)
abstract class NotificationsDatabase : RoomDatabase() {

    abstract fun notificationsDao(): NotificationsDao

    companion object {
        internal const val DB_NAME = "notifications.db"
    }
}

internal class Notification1to2Migration : Migration(1, 2) {

    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL("DELETE FROM $TABLE_NAME")
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object NotificationsDatabaseConstructor : RoomDatabaseConstructor<NotificationsDatabase> {
    override fun initialize(): NotificationsDatabase
}
