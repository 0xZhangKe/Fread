package com.zhangke.fread.common.utils

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import kotlinx.serialization.encodeToString

class ListStringConverter {

    @TypeConverter
    fun fromStringList(text: String): List<String> {
        return globalJson.decodeFromString(text)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return globalJson.encodeToString(list)
    }
}