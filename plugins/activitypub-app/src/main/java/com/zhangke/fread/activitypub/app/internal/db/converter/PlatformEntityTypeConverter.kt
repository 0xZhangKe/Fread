package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.fread.activitypub.app.internal.db.ActivityPubLoggedAccountEntity

class PlatformEntityTypeConverter {

    @TypeConverter
    fun fromType(platform: ActivityPubLoggedAccountEntity.BlogPlatformEntity): String {
        return globalGson.toJson(platform)
    }

    @TypeConverter
    fun toType(text: String): ActivityPubLoggedAccountEntity.BlogPlatformEntity {
        return globalGson.fromJson(text, ActivityPubLoggedAccountEntity.BlogPlatformEntity::class.java)
    }
}