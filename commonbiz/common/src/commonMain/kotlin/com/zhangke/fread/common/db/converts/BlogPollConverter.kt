package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.blog.BlogPoll
import kotlinx.serialization.encodeToString

class BlogPollConverter {

    @TypeConverter
    fun fromStringList(text: String?): BlogPoll? {
        if (text.isNullOrEmpty()) return null
        return globalJson.decodeFromString(text)
    }

    @TypeConverter
    fun toStringList(poll: BlogPoll?): String? {
        if (poll == null) return null
        return globalJson.encodeToString(poll)
    }
}
