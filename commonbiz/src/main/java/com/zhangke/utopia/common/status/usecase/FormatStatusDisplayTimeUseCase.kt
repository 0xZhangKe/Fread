package com.zhangke.utopia.common.status.usecase

import android.content.Context
import com.zhangke.utopia.commonbiz.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class FormatStatusDisplayTimeUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())

    operator fun invoke(
        datetime: Long,
        config: DatetimeFormatConfig = defaultFormatConfig(context),
    ): String {
        val duration = (System.currentTimeMillis() - datetime).milliseconds
        val inWholeDays = duration.inWholeDays
        if (inWholeDays > 3) {
            return dateFormat.format(Date(datetime))
        }
        return "${formatDuration(config, duration)} ${config.ago}"
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

fun defaultFormatConfig(context: Context) = DatetimeFormatConfig(
    ago = context.getString(R.string.date_time_ago),
    day = context.getString(R.string.date_time_day),
    hour = context.getString(R.string.date_time_hour),
    minutes = context.getString(R.string.date_time_minute),
    second = context.getString(R.string.date_time_second),
)
