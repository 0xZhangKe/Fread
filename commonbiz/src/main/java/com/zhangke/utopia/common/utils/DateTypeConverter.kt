package com.zhangke.utopia.common.utils

import androidx.room.TypeConverter
import java.util.Date

class DateTypeConverter {

    @TypeConverter
    fun fromString(time: Long): Date {
        return Date(time)
    }

    @TypeConverter
    fun toString(date: Date): Long {
        return date.time
    }
}
