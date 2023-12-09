package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.uri.StatusProviderUri

class StatusProviderUriListConverter {

    @TypeConverter
    fun fromString(text: String): List<StatusProviderUri> {
        val stringList: List<String> =
            globalGson.fromJson(text, object : TypeToken<List<String>>() {}.type)
        return stringList.map { StatusProviderUri.from(it)!! }
    }

    @TypeConverter
    fun toString(uriList: List<StatusProviderUri>): String {
        val stringList = uriList.map { it.toString() }
        return globalGson.toJson(stringList).toString()
    }
}
