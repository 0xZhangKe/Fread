package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.model.Emoji
import kotlinx.serialization.encodeToString

class EmojiListConverter {

    @TypeConverter
    fun toJsonString(blogAuthor: List<Emoji>): String {
        return globalJson.encodeToString(blogAuthor)
    }

    @TypeConverter
    fun toEmoji(jsonString: String): List<Emoji> {
        return globalJson.decodeFromString(jsonString)
    }
}
