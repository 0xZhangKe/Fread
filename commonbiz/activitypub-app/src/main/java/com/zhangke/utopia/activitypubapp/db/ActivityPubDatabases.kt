package com.zhangke.utopia.activitypubapp.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceDao
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceEntry
import com.zhangke.utopia.activitypubapp.source.user.UserSourceDao
import com.zhangke.utopia.activitypubapp.source.user.UserSourceEntry

internal const val ACTIVITY_PUB_DB_NAME = "ActivityPubStatusProvider"
private const val DB_VERSION = 1

@TypeConverters(
    WebFingerConverter::class,
    TimelineSourceTypeConverter::class,
)
@Database(
    entities = [UserSourceEntry::class, TimelineSourceEntry::class],
    version = DB_VERSION,
    exportSchema = false
)
internal abstract class ActivityPubDatabases : RoomDatabase() {

    abstract fun getUserSourceDao(): UserSourceDao

    abstract fun getTimelineSourceDao(): TimelineSourceDao

    companion object {

        val instance: ActivityPubDatabases by lazy { createDatabase() }

        private fun createDatabase(): ActivityPubDatabases {
            return Room.databaseBuilder(
                appContext,
                ActivityPubDatabases::class.java,
                ACTIVITY_PUB_DB_NAME
            ).build()
        }
    }
}