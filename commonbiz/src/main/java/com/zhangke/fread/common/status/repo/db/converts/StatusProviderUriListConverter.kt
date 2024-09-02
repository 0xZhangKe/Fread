package com.zhangke.fread.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.serialization.encodeToString

class StatusProviderUriListConverter {

    @TypeConverter
    fun fromString(text: String?): List<FormalUri>? {
        text ?: return null
        val stringList: List<String> = globalJson.decodeFromString(text)
        return stringList.map(::stringToUri)
    }

    @TypeConverter
    fun toString(uriList: List<FormalUri>?): String? {
        uriList ?: return null
        val stringList = uriList.map(::uriToString)
        return globalJson.encodeToString(stringList)
    }

    private fun stringToUri(string: String): FormalUri {
        return FormalUri.from(string)!!
    }

    private fun uriToString(uri: FormalUri): String {
        return uri.toString()
    }
}
