@file:OptIn(ExperimentalTime::class)

package com.zhangke.fread.rss.internal.db.converter

import androidx.room.TypeConverter
import com.zhangke.framework.datetime.Instant
import kotlin.time.ExperimentalTime

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
