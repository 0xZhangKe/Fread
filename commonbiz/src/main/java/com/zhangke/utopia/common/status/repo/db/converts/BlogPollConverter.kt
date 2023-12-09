package com.zhangke.utopia.common.status.repo.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.utopia.status.blog.BlogPoll

class BlogPollConverter {

    @TypeConverter
    fun fromStringList(text: String?): BlogPoll? {
        if (text.isNullOrEmpty()) return null
        return globalGson.fromJson(text, BlogPoll::class.java)
    }

    @TypeConverter
    fun toStringList(poll: BlogPoll?): String? {
        if (poll == null) return null
        return globalGson.toJson(poll).toString()
    }
}
