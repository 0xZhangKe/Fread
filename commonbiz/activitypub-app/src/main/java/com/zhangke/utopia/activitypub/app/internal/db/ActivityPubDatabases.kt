package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.internal.source.timeline.TimelineSourceDao
import com.zhangke.utopia.activitypub.app.internal.source.timeline.TimelineSourceEntry
import com.zhangke.utopia.activitypub.app.internal.source.user.UserSourceDao
import com.zhangke.utopia.activitypub.app.internal.source.user.UserSourceEntry
import com.zhangke.utopia.activitypub.app.internal.account.repo.ActivityPubLoggerAccountDao
import com.zhangke.utopia.activitypub.app.internal.account.repo.ActivityPubLoggedAccountEntity
import com.zhangke.utopia.status.utils.UtopiaPlatformTypeConverter

internal const val ACTIVITY_PUB_DB_NAME = "ActivityPubStatusProvider"
private const val DB_VERSION = 1

@TypeConverters(
    WebFingerConverter::class,
    TimelineSourceTypeConverter::class,
    UtopiaPlatformTypeConverter::class,
    ActivityPubUserTokenConverter::class,
)
@Database(
    entities = [UserSourceEntry::class, TimelineSourceEntry::class, ActivityPubLoggedAccountEntity::class],
    version = DB_VERSION,
    exportSchema = false
)
abstract class ActivityPubDatabases : RoomDatabase() {

    abstract fun getUserSourceDao(): UserSourceDao

    abstract fun getTimelineSourceDao(): TimelineSourceDao

    abstract fun getActivityPubUserDao(): ActivityPubLoggerAccountDao

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
