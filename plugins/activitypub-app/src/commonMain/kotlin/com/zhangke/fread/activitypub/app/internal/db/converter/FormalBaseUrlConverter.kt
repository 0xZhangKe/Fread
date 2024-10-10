package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.network.FormalBaseUrl

class FormalBaseUrlConverter {

    @TypeConverter
    fun fromBaseUrl(baseUrl: FormalBaseUrl): String {
        return baseUrl.toString()
    }

    @TypeConverter
    fun toBaseUrl(text: String): FormalBaseUrl {
        return FormalBaseUrl.parse(text)!!
    }
}
