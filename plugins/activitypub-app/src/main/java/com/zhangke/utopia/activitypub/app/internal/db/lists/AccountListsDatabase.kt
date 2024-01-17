package com.zhangke.utopia.activitypub.app.internal.db.lists

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.activitypub.entities.ActivityPubListEntity
import kotlinx.coroutines.flow.Flow

private const val DB_VERSION = 1
private const val DB_NAME = "account_lists.db"
private const val TABLE_NAME = "account_lists"

@Entity(tableName = TABLE_NAME)
data class AccountListsEntity(
    @PrimaryKey val accountId: String,
    val lists: List<ActivityPubListEntity>,
)

@Dao
interface AccountListsDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE accountId = :accountId")
    fun observeAccountLists(accountId: String): Flow<List<AccountListsEntity>>

    @Query("SELECT * FROM $TABLE_NAME WHERE accountId = :accountId")
    suspend fun queryByAccountId(accountId: String): AccountListsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AccountListsEntity)

    @Query("DELETE FROM $TABLE_NAME WHERE accountId=:id")
    suspend fun deleteById(id: String)
}

@TypeConverters(AccountListEntityListConverter::class)
@Database(entities = [AccountListsEntity::class], version = DB_VERSION, exportSchema = false)
abstract class AccountListsDatabase: RoomDatabase(){

    abstract fun getDao(): AccountListsDao

    companion object {

        private var instance: AccountListsDatabase? = null

        fun getInstance(context: Context): AccountListsDatabase {
            if (instance == null) {
                synchronized(AccountListsDatabase::class.java) {
                    if (instance == null) {
                        instance = createDatabase(context)
                    }
                }
            }
            return instance!!
        }

        private fun createDatabase(context: Context): AccountListsDatabase {
            return Room.databaseBuilder(
                context,
                AccountListsDatabase::class.java,
                DB_NAME
            ).build()
        }
    }
}
