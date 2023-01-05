package com.zhangke.utopia.activitypubapp.user

import androidx.room.*
import com.zhangke.framework.utils.appContext

private const val DB_NAME = "activity_pub_users"
private const val DB_VERSION = 1
private const val TABLE_NAME = "logged_users"

@Entity(tableName = TABLE_NAME)
internal data class ActivityPubUserEntry(
    @PrimaryKey val id: String,
    val domain: String,
    val name: String,
    val avatar: String?,
    val description: String?,
    val homePage: String?,
    val accessToken: String,
    val tokenType: String,
    val scope: String,
    val createdAt: String,
    val selected: Boolean,
)

@Dao
internal interface ActivityPubUserDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<ActivityPubUserEntry>

    @Query("SELECT * FROM $TABLE_NAME WHERE id=:id")
    suspend fun queryById(id: String): ActivityPubUserEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ActivityPubUserEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entries: List<ActivityPubUserEntry>)

    @Query("DELETE FROM $TABLE_NAME WHERE id=:id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun nukeTable()
}

@Database(entities = [ActivityPubUserEntry::class], version = DB_VERSION)
internal abstract class ActivityPubUserDatabase : RoomDatabase() {

    abstract fun getActivityPubUserDao(): ActivityPubUserDao

    companion object {

        val instance: ActivityPubUserDatabase by lazy { createDatabase() }

        private fun createDatabase(): ActivityPubUserDatabase {
            return Room.databaseBuilder(appContext, ActivityPubUserDatabase::class.java, DB_NAME)
                .build()
        }
    }
}