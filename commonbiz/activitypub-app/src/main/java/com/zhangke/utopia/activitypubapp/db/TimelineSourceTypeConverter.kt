package com.zhangke.utopia.activitypubapp.db

import androidx.room.TypeConverter
import com.zhangke.utopia.activitypubapp.source.timeline.TimelineSourceType

internal class TimelineSourceTypeConverter {

    @TypeConverter
    fun fromType(type: TimelineSourceType): String {
        return type.stringValue
    }

    @TypeConverter
    fun toType(text: String): TimelineSourceType {
        return TimelineSourceType.valueOf(text)
    }
}