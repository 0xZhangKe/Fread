package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.google.gson.JsonArray
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.blog.BlogMedia

class BlogMediaListConverter {

    private val mediaConvertHelper = BlogMediaConverterHelper()

    @TypeConverter
    fun fromStringList(text: String): List<BlogMedia> {
        val jsonArray = globalGson.fromJson(text, JsonArray::class.java)
        val list = mutableListOf<BlogMedia>()
        jsonArray.forEach { element ->
            mediaConvertHelper.fromJsonObject(element.asJsonObject)?.let { list += it }
        }
        return list
    }

    @TypeConverter
    fun toStringList(list: List<BlogMedia>): String {
        val jsonArray = JsonArray()
        list.forEach { media ->
            mediaConvertHelper.toJsonObject(media)?.let { jsonArray.add(it) }
        }
        return jsonArray.toString()
    }
}
