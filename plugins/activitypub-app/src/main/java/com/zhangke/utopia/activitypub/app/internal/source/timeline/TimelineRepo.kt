package com.zhangke.utopia.activitypub.app.internal.source.timeline

import androidx.room.*
import com.zhangke.utopia.activitypub.app.internal.db.ActivityPubDatabases
import javax.inject.Inject

private const val TABLE_NAME = "TimelineSources"

@Entity(tableName = TABLE_NAME, primaryKeys = ["host", "type"])
data class TimelineSourceEntry(
    val host: String,
    val type: TimelineSourceType,
)

@Dao
interface TimelineSourceDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE host=:host AND type=:type")
    suspend fun query(host: String, type: TimelineSourceType): TimelineSourceEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: TimelineSourceEntry)
}

class TimelineRepo @Inject constructor(
    private val databases: ActivityPubDatabases
) {

    private val dao: TimelineSourceDao get() = databases.getTimelineSourceDao()

    suspend fun query(host: String, type: TimelineSourceType): TimelineSource? {
        return dao.query(host, type)?.toSource()
    }

    suspend fun save(timelineSource: TimelineSource) {
        dao.insert(timelineSource.toEntry())
    }

    private fun TimelineSourceEntry.toSource(): TimelineSource {
        return TimelineSource(host = host, type = type)
    }

    private fun TimelineSource.toEntry(): TimelineSourceEntry {
        return TimelineSourceEntry(host = host, type = type)
    }
}