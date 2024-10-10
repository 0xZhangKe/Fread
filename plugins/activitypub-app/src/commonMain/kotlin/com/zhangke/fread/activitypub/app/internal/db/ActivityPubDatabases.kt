package com.zhangke.fread.activitypub.app.internal.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubInstanceEntityConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.ActivityPubUserTokenConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.EmojiListConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.FormalBaseUrlConverter
import com.zhangke.fread.activitypub.app.internal.db.converter.PlatformEntityTypeConverter
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
        ActivityPubLoggedAccountEntity::class,
        ActivityPubApplicationEntity::class,
        ActivityPubInstanceInfoEntity::class,
        WebFingerBaseurlToIdEntity::class,
    ],
    version = DB_VERSION,
    exportSchema = false
)
abstract class ActivityPubDatabases : RoomDatabase() {

    abstract fun getLoggedAccountDao(): ActivityPubLoggerAccountDao

    abstract fun getApplicationDao(): ActivityPubApplicationsDao

    abstract fun getPlatformDao(): ActivityPubPlatformDao

    abstract fun getUserIdDao(): WebFingerBaseurlToIdDao

    companion object {
        internal const val DB_NAME = "ActivityPubStatusProvider"
    }
}
