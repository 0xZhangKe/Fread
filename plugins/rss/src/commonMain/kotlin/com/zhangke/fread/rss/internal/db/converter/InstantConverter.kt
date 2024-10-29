package com.zhangke.fread.rss.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.datetime.Instant

class InstantConverter {

    @TypeConverter
    fun convertToLong(instant: Instant): Long {
        return instant.instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun convertToInstant(value: Long): Instant {
        return Instant(kotlinx.datetime.Instant.fromEpochMilliseconds(value))
    }
}
