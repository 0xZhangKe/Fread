package com.zhangke.utopia.status.utils

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.platform.UtopiaPlatform

class UtopiaPlatformTypeConverter {

    @TypeConverter
    fun fromType(platform: UtopiaPlatform): String {
        return globalGson.toJson(platform)
    }

    @TypeConverter
    fun toType(text: String): UtopiaPlatform {
        return globalGson.fromJson(text, UtopiaPlatform::class.java)
    }
}
