package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalJson
import com.zhangke.fread.status.author.BlogAuthor
import kotlinx.serialization.encodeToString

class BlogAuthorConverter {

    @TypeConverter
    fun toBlogAuthorString(blogAuthor: BlogAuthor): String {
        return globalJson.encodeToString(blogAuthor)
    }

    @TypeConverter
    fun toBlogAuthor(jsonString: String): BlogAuthor {
        return globalJson.decodeFromString(jsonString)
    }
}
