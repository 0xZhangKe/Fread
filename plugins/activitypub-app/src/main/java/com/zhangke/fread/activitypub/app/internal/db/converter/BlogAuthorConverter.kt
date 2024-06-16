package com.zhangke.fread.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.architect.json.globalGson
import com.zhangke.fread.status.author.BlogAuthor

class BlogAuthorConverter {

    @TypeConverter
    fun toBlogAuthorString(blogAuthor: BlogAuthor): String {
        return globalGson.toJson(blogAuthor)
    }

    @TypeConverter
    fun toBlogAuthor(jsonString: String): BlogAuthor {
        return globalGson.fromJson(jsonString, BlogAuthor::class.java)
    }
}
