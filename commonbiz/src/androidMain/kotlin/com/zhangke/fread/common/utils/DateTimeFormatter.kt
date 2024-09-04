package com.zhangke.fread.common.utils

import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.date_time_ago
import com.zhangke.fread.commonbiz.date_time_day
import com.zhangke.fread.commonbiz.date_time_hour
import com.zhangke.fread.commonbiz.date_time_minute
import com.zhangke.fread.commonbiz.date_time_second
import org.jetbrains.compose.resources.getString
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object DateTimeFormatter {

    suspend fun format(
        datetime: Long,
    ): String {
        return format(
            datetime = datetime,
            config = defaultFormatConfig(),
        )
    }

    fun format(
        datetime: Long,
        config: DatetimeFormatConfig,
    ): String {
        val duration = (System.currentTimeMillis() - datetime).milliseconds
        val inWholeDays = duration.inWholeDays
        if (inWholeDays > 3) {
            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
            return dateFormat.format(Date(datetime))
        }
        return "${formatDuration(config, duration)}${config.ago}"
    }

    private fun formatDuration(config: DatetimeFormatConfig, duration: Duration): String {
        if (duration.isInfinite()) return ""
        var leftDuration = duration
        val day = (leftDuration).inWholeDays.days
        leftDuration -= day
        if (day > 0.days) {
            return "${day.toInt(DurationUnit.DAYS)} ${config.day}"
        }
        val hours = leftDuration.inWholeHours.hours
        leftDuration -= hours
        if (hours > 0.hours) {
            return "${hours.toInt(DurationUnit.HOURS)} ${config.hour}"
        }
        val minutes = leftDuration.inWholeMinutes.minutes
        leftDuration -= minutes
        if (minutes > 0.minutes) {
            return "${minutes.toInt(DurationUnit.MINUTES)} ${config.minutes}"
        }
        val seconds = leftDuration.inWholeSeconds.seconds
        return "${seconds.toInt(DurationUnit.SECONDS)} ${config.second}"
    }
}

data class DatetimeFormatConfig(
    val ago: String,
    val day: String,
    val hour: String,
    val minutes: String,
    val second: String,
)

suspend fun defaultFormatConfig() = DatetimeFormatConfig(
    ago = getString(Res.string.date_time_ago),
    day = getString(Res.string.date_time_day),
    hour = getString(Res.string.date_time_hour),
    minutes = getString(Res.string.date_time_minute),
    second = getString(Res.string.date_time_second),
)
