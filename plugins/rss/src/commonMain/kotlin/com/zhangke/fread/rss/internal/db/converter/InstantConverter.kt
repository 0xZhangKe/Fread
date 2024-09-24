package com.zhangke.fread.rss.internal.db.converter

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

class InstantConverter {

    @TypeConverter
    fun convertToLong(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun convertToInstant(value: Long): Instant {
        return Instant.fromEpochMilliseconds(value)
    }
}