package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson

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