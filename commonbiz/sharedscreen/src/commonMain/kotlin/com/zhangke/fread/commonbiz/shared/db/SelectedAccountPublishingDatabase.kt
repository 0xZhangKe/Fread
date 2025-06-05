package com.zhangke.fread.commonbiz.shared.db

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

private const val TABLE_NAME = "selected_account_publishing"

@Entity(tableName = TABLE_NAME)
data class SelectedAccountPublishing(
    @PrimaryKey val accountUri: String
)

@Dao
interface SelectedAccountPublishingDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun getAll(): List<SelectedAccountPublishing>

    @Insert
    suspend fun insert(items: List<SelectedAccountPublishing>)

    @Query("DELETE FROM $TABLE_NAME")
    suspend fun deleteTable()

}

@Database(
    entities = [SelectedAccountPublishing::class],
    version = 1,
    exportSchema = false,
)
@ConstructedBy(SelectedAccountPublishingDatabaseConstructor::class)
abstract class SelectedAccountPublishingDatabase : RoomDatabase() {

    abstract fun dao(): SelectedAccountPublishingDao

    companion object {

        const val DB_NAME = "SelectedAccountPublishing.db"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SelectedAccountPublishingDatabaseConstructor :
    RoomDatabaseConstructor<SelectedAccountPublishingDatabase> {
    override fun initialize(): SelectedAccountPublishingDatabase
}

