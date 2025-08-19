package com.zhangke.fread.activitypub.app.internal.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubInstanceEntityConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubUserTokenConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.EmojiListConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.FormalBaseUrlConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.PlatformEntityTypeConverter
import com.zhangke.fread.activitypub.app.internal.db.old.OldActivityPubLoggedAccountEntity
import com.zhangke.fread.activitypub.app.internal.db.old.OldActivityPubLoggerAccountDao
import com.zhangke.fread.common.utils.WebFingerConverter

private const val DB_VERSION = 1

@TypeConverters(
    WebFingerConverter::class,
    PlatformEntityTypeConverter::class,
    ActivityPubUserTokenConverter::class,
    ActivityPubInstanceEntityConverter::class,
    FormalBaseUrlConverter::class,
    EmojiListConverter::class,
)
@Database(
    entities = [
        OldActivityPubLoggedAccountEntity::class,
        ActivityPubApplicationEntity::class,
        ActivityPubInstanceInfoEntity::class,
        WebFingerBaseurlToIdEntity::class,
    ],
    version = DB_VERSION,
    exportSchema = false
)
@ConstructedBy(ActivityPubDatabasesConstructor::class)
abstract class ActivityPubDatabases : RoomDatabase() {

    abstract fun getLoggedAccountDao(): OldActivityPubLoggerAccountDao

    abstract fun getApplicationDao(): ActivityPubApplicationsDao

    abstract fun getPlatformDao(): ActivityPubPlatformDao

    abstract fun getUserIdDao(): WebFingerBaseurlToIdDao

    companion object {
        internal const val DB_NAME = "ActivityPubStatusProvider"
    }
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ActivityPubDatabasesConstructor : RoomDatabaseConstructor<ActivityPubDatabases> {
    override fun initialize(): ActivityPubDatabases
}
