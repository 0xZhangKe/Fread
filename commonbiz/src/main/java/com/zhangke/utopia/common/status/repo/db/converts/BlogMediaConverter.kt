package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.blog.BlogMediaMeta

class BlogMediaConverter {

    @TypeConverter
    fun fromStringList(text: String?): BlogMediaMeta? {
        if (text.isNullOrEmpty()) return null
        return globalGson.fromJson(text, BlogMediaMeta::class.java)
    }

    @TypeConverter
    fun toStringList(poll: BlogMediaMeta?): String? {
        if (poll == null) return null
        return globalGson.toJson(poll).toString()
    }
}
