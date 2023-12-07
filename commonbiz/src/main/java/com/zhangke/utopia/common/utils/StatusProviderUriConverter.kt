package com.zhangke.utopia.common.utils

import androidx.room.TypeConverter
import com.zhangke.utopia.status.uri.StatusProviderUri

class StatusProviderUriConverter {

    @TypeConverter
    fun fromString(uri: String): StatusProviderUri {
        return StatusProviderUri.from(uri)!!
    }

    @TypeConverter
    fun toString(uri: StatusProviderUri): String {
        return uri.toString()
    }
}