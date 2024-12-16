package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.blog.BlogMedia
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject

class BlogMediaListConverter {

    private val mediaConvertHelper = BlogMediaConverterHelper()

    @TypeConverter
    fun fromStringList(text: String): List<BlogMedia> {
        val jsonArray: JsonArray = globalJson.decodeFromString(text)
        val list = mutableListOf<BlogMedia>()
        jsonArray.forEach { element ->
            mediaConvertHelper.fromJsonObject(element.jsonObject)?.let { list += it }
        }
        return list
    }

    @TypeConverter
    fun toStringList(list: List<BlogMedia>): String {
        return JsonArray(
            buildList {
                list.forEach { media ->
                    mediaConvertHelper.toJsonObject(media)?.let { add(it) }
                }
            },
        ).toString()
    }
}
