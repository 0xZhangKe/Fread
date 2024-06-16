package com.zhangke.fread.common.utils

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.zhangke.framework.architect.json.globalGson

class ListStringConverter {

    @TypeConverter
    fun fromStringList(text: String): List<String> {
        return globalGson.fromJson(text, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun toStringList(list: List<String>): String {
        return globalGson.toJson(list).toString()
    }
}