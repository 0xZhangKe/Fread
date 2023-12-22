package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.uri.FormalUri

class StatusProviderUriListConverter {

    @TypeConverter
    fun fromString(text: String): List<FormalUri> {
        val stringList: List<String> =
            globalGson.fromJson(text, object : TypeToken<List<String>>() {}.type)
        return stringList.map(::stringToUri)
    }

    @TypeConverter
    fun toString(uriList: List<FormalUri>): String {
        val stringList = uriList.map(::uriToString)
        return globalGson.toJson(stringList).toString()
    }

    internal fun stringToUri(string: String): FormalUri {
        return FormalUri.from(string)!!
    }

    internal fun uriToString(uri: FormalUri): String {
        return uri.toString()
    }
}
