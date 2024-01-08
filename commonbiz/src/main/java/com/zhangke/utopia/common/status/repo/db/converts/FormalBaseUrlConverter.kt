package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.network.FormalBaseUrl

class FormalBaseUrlConverter {

    @TypeConverter
    fun fromString(text: String): FormalBaseUrl {
        return FormalBaseUrl.parse(text)!!
    }

    @TypeConverter
    fun toString(baseUrl: FormalBaseUrl): String {
        return baseUrl.toString()
    }
}
