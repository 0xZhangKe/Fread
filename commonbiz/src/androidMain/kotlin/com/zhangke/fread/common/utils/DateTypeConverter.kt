package com.zhangke.fread.common.utils

import androidx.room.TypeConverter
import java.util.Date

class DateTypeConverter {

    @TypeConverter
    fun fromLong(time: Long): Date {
        return Date(time)
    }

    @TypeConverter
    fun toLong(date: Date): Long {
        return date.time
    }
}
