package com.zhangke.fread.common.db.converts

import androidx.room.TypeConverter
import com.zhangke.fread.status.model.ContentType

class ContentTypeConverter {

    @TypeConverter
    fun fromString(text: String): ContentType {
        return ContentType.valueOf(text)
    }

    @TypeConverter
    fun toString(type: ContentType): String {
        return type.name
    }
}
