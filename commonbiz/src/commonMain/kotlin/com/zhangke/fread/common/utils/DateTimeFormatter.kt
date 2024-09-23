package com.zhangke.fread.common.utils

import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.date_time_ago
import com.zhangke.fread.commonbiz.date_time_day
import com.zhangke.fread.commonbiz.date_time_hour
import com.zhangke.fread.commonbiz.date_time_minute
import com.zhangke.fread.commonbiz.date_time_second
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import org.jetbrains.compose.resources.getString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
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
        val instant = Instant.fromEpochMilliseconds(datetime)
        val duration = Clock.System.now() - instant
        if (duration > 3.days) {
            // TODO: format with locale
            return instant.format(
                DateTimeComponents.Format {
                    year()
                    char('-')
                    monthNumber(Padding.ZERO)
                    char('-')
                    dayOfMonth()
                }
            )
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
