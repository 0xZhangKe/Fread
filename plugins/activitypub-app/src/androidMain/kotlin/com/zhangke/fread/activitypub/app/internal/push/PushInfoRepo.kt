package com.zhangke.fread.activitypub.app.internal.push

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

private const val DB_VERSION = 1
private const val TABLE_NAME = "PushInfo"

@Entity(tableName = TABLE_NAME)
data class PushInfo(
    @PrimaryKey val accountId: String,
    val publicKey: String,
    val privateKey: String,
    val authKey: String,
)

@Dao
interface PushInfoDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE accountId = :accountId")
    suspend fun getPushInfo(accountId: String): PushInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(info: PushInfo)

    @Query("DELETE FROM $TABLE_NAME WHERE accountId = :accountId")
    suspend fun delete(accountId: String)
}

@Database(
    entities = [PushInfo::class],
    version = DB_VERSION,
    exportSchema = false,
)
abstract class PushInfoDatabase : RoomDatabase() {

    abstract fun pushInfoDao(): PushInfoDao

    companion object {

        const val DB_NAME = "ActivityPubPushInfo.db"
    }
}

class PushInfoRepo(database: PushInfoDatabase) {

    private val pushDao = database.pushInfoDao()

    suspend fun getPushInfo(accountId: String): PushInfo? {
        return pushDao.getPushInfo(accountId)
    }

    suspend fun insert(info: PushInfo) {
        pushDao.insert(info)
    }

    suspend fun delete(accountId: String) {
        pushDao.delete(accountId)
    }
}
