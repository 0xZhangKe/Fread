package com.zhangke.utopia.common.status.repo.db

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.blog.BlogMedia

class BlogMediaListConverter {

    @TypeConverter
    fun fromStringList(text: String): List<BlogMedia> {
        return globalGson.fromJson(text, object : TypeToken<List<BlogMedia>>() {}.type)
    }

    @TypeConverter
    fun toStringList(list: List<BlogMedia>): String {
        return globalGson.toJson(list).toString()
    }
}
