package com.zhangke.utopia.activitypub.app.internal.db

import androidx.room.TypeConverter
import com.zhangke.utopia.activitypub.app.internal.source.timeline.TimelineSourceType

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