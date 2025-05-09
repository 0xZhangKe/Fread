package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.network.FormalBaseUrl

class FormalBaseUrlConverter {

    @TypeConverter
    fun fromString(text: String?): FormalBaseUrl? {
        text ?: return null
        return FormalBaseUrl.parse(text)!!
    }

    @TypeConverter
    fun toString(baseUrl: FormalBaseUrl?): String? {
        baseUrl ?: return null
        return baseUrl.toString()
    }
}
