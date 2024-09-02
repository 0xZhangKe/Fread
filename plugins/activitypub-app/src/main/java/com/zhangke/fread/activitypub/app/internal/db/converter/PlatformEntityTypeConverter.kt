package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountEntity
import kotlinx.serialization.encodeToString

class PlatformEntityTypeConverter {

    @TypeConverter
    fun fromType(platform: ActivityPubLoggedAccountEntity.BlogPlatformEntity): String {
        return globalJson.encodeToString(platform)
    }

    @TypeConverter
    fun toType(text: String): ActivityPubLoggedAccountEntity.BlogPlatformEntity {
        return globalJson.decodeFromString(text)
    }
}