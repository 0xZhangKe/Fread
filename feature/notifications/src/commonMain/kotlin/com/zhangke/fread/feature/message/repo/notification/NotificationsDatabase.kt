package com.zhangke.fread.feature.message.repo.notification

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
import com.zhangke.fread.common.db.converts.FormalUriConverter
import com.zhangke.fread.common.db.converts.StatusNotificationConverter
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.uri.FormalUri

private const val DB_VERSION = 1
private const val TABLE_NAME = "notifications"

@Entity(tableName = TABLE_NAME)
data class NotificationEntity(
    @PrimaryKey val notificationId: String,
    val accountUri: FormalUri,
    val notification: StatusNotification,
)

@Dao
interface NotificationsDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE notificationId = :id")
    suspend fun queryById(id: String): NotificationEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE accountUri = :accountUri")
    suspend fun queryByAccountUri(accountUri: FormalUri): List<NotificationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<NotificationEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE accountUri = :accountUri")
    suspend fun delete(accountUri: FormalUri)
}

@TypeConverters(
    FormalUriConverter::class,
    StatusNotificationConverter::class,
)
@Database(entities = [NotificationEntity::class], version = DB_VERSION, exportSchema = false)
@ConstructedBy(NotificationsDatabaseConstructor::class)
abstract class NotificationsDatabase : RoomDatabase() {

    abstract fun notificationsDao(): NotificationsDao

    companion object {

        internal const val DB_NAME = "all_accounts_notifications.db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object NotificationsDatabaseConstructor : RoomDatabaseConstructor<NotificationsDatabase> {
    override fun initialize(): NotificationsDatabase
}
