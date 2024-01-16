package com.zhangke.utopia.activitypub.app.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType

internal class TimelineSourceTypeConverter {

    @TypeConverter
    fun fromType(type: TimelineSourceType): String {
        return type.name
    }

    @TypeConverter
    fun toType(text: String): TimelineSourceType {
        return TimelineSourceType.valueOf(text)
    }
}