package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.fread.status.model.Emoji

class EmojiListConverter {

    @TypeConverter
    fun toJsonString(blogAuthor: List<Emoji>): String {
        return globalGson.toJson(blogAuthor)
    }

    @TypeConverter
    fun toEmoji(jsonString: String): List<Emoji> {
        return globalGson.fromJson(jsonString, object : TypeToken<List<Emoji>>() {}.type)
    }
}
