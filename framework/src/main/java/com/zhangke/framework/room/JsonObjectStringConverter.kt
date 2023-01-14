package com.zhangke.framework.room

import androidx.room.TypeConverter
import com.google.gson.JsonObject
import com.zhangke.framework.architect.json.globalGson

class JsonObjectStringConverter {

    @TypeConverter
    fun fromJsonObject(jsonObject: JsonObject): String {
        return jsonObject.toString()
    }

    @TypeConverter
    fun toJsonObject(text: String): JsonObject {
        if (text.isEmpty()) return JsonObject()
        return globalGson.fromJson(text, JsonObject::class.java)
    }
}