package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.internal.repo.source.TimelineSourceDao
import com.zhangke.utopia.activitypub.app.internal.repo.source.TimelineSourceEntry
import com.zhangke.utopia.activitypub.app.internal.repo.source.UserSourceDao
import com.zhangke.utopia.activitypub.app.internal.repo.source.UserSourceEntry

internal const val ACTIVITY_PUB_DB_NAME = "ActivityPubStatusProvider"
private const val DB_VERSION = 1

@TypeConverters(
    WebFingerConverter::class,
    TimelineSourceTypeConverter::class,
    PlatformEntityTypeConverter::class,
    ActivityPubUserTokenConverter::class,
)
@Database(
    entities = [UserSourceEntry::class, TimelineSourceEntry::class, ActivityPubLoggedAccountEntity::class, ActivityPubApplicationEntity::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class ActivityPubDatabases : RoomDatabase() {

    abstract fun getUserSourceDao(): UserSourceDao

    abstract fun getTimelineSourceDao(): TimelineSourceDao

    abstract fun getActivityPubUserDao(): ActivityPubLoggerAccountDao

    abstract fun getApplicationDao(): ActivityPubApplicationsDao

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
