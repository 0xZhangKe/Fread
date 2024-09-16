package com.zhangke.framework.utils

import com.zhangke.fread.framework.Res
import com.zhangke.fread.framework.duration_day
import com.zhangke.fread.framework.duration_hour
import com.zhangke.fread.framework.duration_minute
import com.zhangke.fread.framework.duration_week
import org.jetbrains.compose.resources.getString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

suspend fun Duration.formattedString(): String {
    val builder = StringBuilder()
    val formattedDuration = format()
    val weeks = dayToWeek(formattedDuration.days)
    if (weeks > 0) {
        builder.append("$weeks${getString(Res.string.duration_week)}")
    }
    val days = (formattedDuration.days - weeks * 7).coerceAtLeast(0)
    if (days > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("$days${getString(Res.string.duration_day)}")
    }
    if (formattedDuration.hours > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("${formattedDuration.hours}${getString(Res.string.duration_hour)}")
    }
    if (formattedDuration.minutes > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("${formattedDuration.minutes}${getString(Res.string.duration_minute)}")
    }
    return builder.toString()
}

fun dayToWeek(day: Int): Int {
    return day / 7
}

fun Duration.format(): FormattedDuration {
    var leftDuration = this
    val days = leftDuration.inWholeDays.toInt()
    leftDuration -= days.days
    val hours = leftDuration.inWholeHours.toInt()
    leftDuration -= hours.hours
    val minutes = leftDuration.inWholeMinutes.toInt()
    leftDuration -= minutes.minutes
    return FormattedDuration(days = days, hours = hours, minutes = minutes)
}

data class FormattedDuration(
    val days: Int,
    val hours: Int,
    val minutes: Int,
)
