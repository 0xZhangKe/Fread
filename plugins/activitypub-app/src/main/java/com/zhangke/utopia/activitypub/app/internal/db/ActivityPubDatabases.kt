package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.framework.utils.appContext
import com.zhangke.utopia.activitypub.app.internal.db.converter.ActivityPubInstanceEntityConverter
import com.zhangke.utopia.activitypub.app.internal.db.converter.ActivityPubUserTokenConverter
import com.zhangke.utopia.activitypub.app.internal.db.converter.PlatformEntityTypeConverter
import com.zhangke.utopia.activitypub.app.internal.db.converter.TimelineSourceTypeConverter
import com.zhangke.utopia.common.utils.WebFingerConverter

internal const val ACTIVITY_PUB_DB_NAME = "ActivityPubStatusProvider"
private const val DB_VERSION = 1

@TypeConverters(
    WebFingerConverter::class,
    TimelineSourceTypeConverter::class,
    PlatformEntityTypeConverter::class,
    ActivityPubUserTokenConverter::class,
    ActivityPubInstanceEntityConverter::class,
)
@Database(
    entities = [
        ActivityPubLoggedAccountEntity::class,
        ActivityPubApplicationEntity::class,
        ActivityPubInstanceInfoEntity::class,
        WebFingerToBaseUrlEntity::class,
    ],
    version = DB_VERSION,
    exportSchema = false
)
abstract class ActivityPubDatabases : RoomDatabase() {

    abstract fun getLoggedAccountDao(): ActivityPubLoggerAccountDao

    abstract fun getApplicationDao(): ActivityPubApplicationsDao

    abstract fun getPlatformDao(): ActivityPubPlatformDao

    abstract fun getWebFingerToBaseUrlDao(): WebFingerToBaseUrlDao

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
