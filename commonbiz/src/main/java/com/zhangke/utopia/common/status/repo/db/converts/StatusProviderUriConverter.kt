package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.utopia.status.uri.FormalUri

class StatusProviderUriConverter {

    @TypeConverter
    fun fromString(uri: String): FormalUri {
        return FormalUri.from(uri)!!
    }

    @TypeConverter
    fun toString(uri: FormalUri): String {
        return uri.toString()
    }
}