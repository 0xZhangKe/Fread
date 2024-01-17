package com.zhangke.utopia.activitypub.app.internal.db.status

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.db.converter.ActivityPubStatusEntityConverter
import com.zhangke.utopia.activitypub.app.internal.db.converter.ActivityPubStatusSourceTypeConverter
import com.zhangke.utopia.activitypub.app.internal.db.converter.FormalBaseUrlConverter
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType

private const val DB_VERSION = 1
private const val DB_NAME = "activity_pub_status.db"
private const val TABLE_NAME = "activity_pub_status"

/**
 * 目前 Status 设计是进入页面后，先获取本地数据并显示，
 * 然后再获取网络数据，替换所有本地数据。
 * 另外，本地数据库存储的列表都是连续的。
 */
@Entity(tableName = TABLE_NAME)
data class ActivityPubStatusTableEntity(
    @PrimaryKey val id: String,
    val type: ActivityPubStatusSourceType,
    val serverBaseUrl: FormalBaseUrl,
    val listId: String?,
    val status: ActivityPubStatusEntity,
    val createTimestamp: Long,
)

@Dao
interface ActivityPubStatusDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE id = :id")
    suspend fun query(id: String): ActivityPubStatusTableEntity?

    @Query("SELECT * FROM $TABLE_NAME WHERE serverBaseUrl = :serverBaseUrl AND type = :type ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun query(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        limit: Int,
    ): List<ActivityPubStatusTableEntity>

    @Query("SELECT * FROM $TABLE_NAME WHERE serverBaseUrl = :serverBaseUrl AND type = :type AND listId = :listId ORDER BY createTimestamp DESC LIMIT :limit")
    suspend fun queryListStatus(
        serverBaseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        listId: String,
        limit: Int,
    ): List<ActivityPubStatusTableEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ActivityPubStatusTableEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<ActivityPubStatusTableEntity>)

    @Query("DELETE FROM $TABLE_NAME WHERE serverBaseUrl=:baseUrl AND type=:type")
    suspend fun delete(baseUrl: FormalBaseUrl, type: ActivityPubStatusSourceType)

    @Query("DELETE FROM $TABLE_NAME WHERE serverBaseUrl=:baseUrl AND type=:type AND listId=:listId")
    suspend fun deleteListStatus(
        baseUrl: FormalBaseUrl,
        type: ActivityPubStatusSourceType,
        listId: String,
    )

    @Query("DELETE FROM $TABLE_NAME WHERE id=:id")
    suspend fun delete(id: String)
}

@TypeConverters(
    FormalBaseUrlConverter::class,
    ActivityPubStatusSourceTypeConverter::class,
    ActivityPubStatusEntityConverter::class,
)
@Database(
    entities = [ActivityPubStatusTableEntity::class],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class ActivityPubStatusDatabase : RoomDatabase() {

    abstract fun getDao(): ActivityPubStatusDao

    companion object {

        private var instance: ActivityPubStatusDatabase? = null

        fun getInstance(context: Context): ActivityPubStatusDatabase {
            if (instance == null) {
                synchronized(ActivityPubStatusDatabase::class.java) {
                    if (instance == null) {
                        instance = createDatabase(context)
                    }
                }
            }
            return instance!!
        }

        private fun createDatabase(context: Context): ActivityPubStatusDatabase {
            return Room.databaseBuilder(
                context,
                ActivityPubStatusDatabase::class.java,
                DB_NAME
            ).build()
        }
    }
}
