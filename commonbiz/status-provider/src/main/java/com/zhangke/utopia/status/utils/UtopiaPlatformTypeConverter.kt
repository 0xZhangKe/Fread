package com.zhangke.utopia.status.utils

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.platform.BlogPlatform

class UtopiaPlatformTypeConverter {

    @TypeConverter
    fun fromType(platform: BlogPlatform): String {
        return globalGson.toJson(platform)
    }

    @TypeConverter
    fun toType(text: String): BlogPlatform {
        return globalGson.fromJson(text, BlogPlatform::class.java)
    }
}
