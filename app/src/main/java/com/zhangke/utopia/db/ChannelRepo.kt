package com.zhangke.utopia.db

import android.content.Context
import androidx.room.*
import com.zhangke.framework.room.ListStringConverter
import javax.inject.Inject

private const val DB_NAME = "UtopiaChannels.db"
private const val TABLE_NAME = "channels"
private const val DB_VERSION = 1

@TypeConverters(ListStringConverter::class)
@Entity(tableName = TABLE_NAME)
data class Channel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val uriList: List<String>,
)

@Dao
interface ChannelsDao {

    @Query("SELECT * FROM $TABLE_NAME")
    suspend fun queryAll(): List<Channel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(channel: Channel)

    @Delete
    suspend fun delete(channel: Channel)
}

@Database(entities = [Channel::class], version = DB_VERSION, exportSchema = false)
abstract class ChannelsDatabases : RoomDatabase() {

    abstract fun getDao(): ChannelsDao

    companion object {

        fun createDatabase(context: Context): ChannelsDatabases {
            return Room.databaseBuilder(context, ChannelsDatabases::class.java, DB_NAME)
                .build()
        }
    }
}

class ChannelRepo @Inject constructor(databases: ChannelsDatabases) {

    private val channelsDao = databases.getDao()

    suspend fun queryAll(): List<Channel> {
        return channelsDao.queryAll()
    }

    suspend fun insert(channelName: String, uriList: List<String>) {
        insert(Channel(id = 0, name = channelName, uriList))
    }

    suspend fun insert(channel: Channel) {
        channelsDao.insert(channel)
    }

    suspend fun delete(channel: Channel) {
        channelsDao.delete(channel)
    }
}