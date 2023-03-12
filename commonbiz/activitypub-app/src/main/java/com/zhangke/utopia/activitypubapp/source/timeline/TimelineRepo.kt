package com.zhangke.utopia.activitypubapp.source.timeline

import androidx.room.*
import com.zhangke.utopia.activitypubapp.db.ActivityPubDatabases

private const val TABLE_NAME = "TimelineSources"

@Entity(tableName = TABLE_NAME, primaryKeys = ["host", "type"])
internal data class TimelineSourceEntry(
    val host: String,
    val type: TimelineSourceType,
)

@Dao
internal interface TimelineSourceDao {

    @Query("SELECT * FROM $TABLE_NAME WHERE host=:host AND type=:type")
    suspend fun query(host: String, type: TimelineSourceType): TimelineSourceEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: TimelineSourceEntry)
}

internal object TimelineRepo {

    private val dao: TimelineSourceDao get() = ActivityPubDatabases.instance.getTimelineSourceDao()

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