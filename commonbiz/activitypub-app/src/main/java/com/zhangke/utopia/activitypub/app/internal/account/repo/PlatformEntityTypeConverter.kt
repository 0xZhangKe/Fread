package com.zhangke.utopia.activitypub.app.internal.account.repo

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.activitypub.app.internal.account.entities.BlogPlatformEntity

class PlatformEntityTypeConverter {

    @TypeConverter
    fun fromType(platform: BlogPlatformEntity): String {
        return globalGson.toJson(platform)
    }

    @TypeConverter
    fun toType(text: String): BlogPlatformEntity {
        return globalGson.fromJson(text, BlogPlatformEntity::class.java)
    }
}