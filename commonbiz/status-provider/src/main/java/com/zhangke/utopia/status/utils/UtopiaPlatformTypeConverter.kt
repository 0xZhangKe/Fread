package com.zhangke.utopia.status.utils

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.server.StatusProviderServer

class UtopiaPlatformTypeConverter {

    @TypeConverter
    fun fromType(platform: StatusProviderServer): String {
        return globalGson.toJson(platform)
    }

    @TypeConverter
    fun toType(text: String): StatusProviderServer {
        return globalGson.fromJson(text, StatusProviderServer::class.java)
    }
}
