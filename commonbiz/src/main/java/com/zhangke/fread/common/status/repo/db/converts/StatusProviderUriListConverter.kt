package com.zhangke.fread.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.fread.status.uri.FormalUri

class StatusProviderUriListConverter {

    @TypeConverter
    fun fromString(text: String?): List<FormalUri>? {
        text ?: return null
        val stringList: List<String> =
            globalGson.fromJson(text, object : TypeToken<List<String>>() {}.type)
        return stringList.map(::stringToUri)
    }

    @TypeConverter
    fun toString(uriList: List<FormalUri>?): String? {
        uriList ?: return null
        val stringList = uriList.map(::uriToString)
        return globalGson.toJson(stringList).toString()
    }

    private fun stringToUri(string: String): FormalUri {
        return FormalUri.from(string)!!
    }

    private fun uriToString(uri: FormalUri): String {
        return uri.toString()
    }
}
