package com.zhangke.framework.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import com.zhangke.utopia.framework.R

fun Duration.formattedString(): String {
    val context = appContext
    val builder = StringBuilder()
    val formattedDuration = format()
    val weeks = dayToWeek(formattedDuration.days)
    if (weeks > 0) {
        builder.append("$weeks${context.getString(R.string.duration_week)}")
    }
    val days = (formattedDuration.days - weeks * 7).coerceAtLeast(0)
    if (days > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("$days${context.getString(R.string.duration_day)}")
    }
    if (formattedDuration.hours > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("${formattedDuration.hours}${context.getString(R.string.duration_hour)}")
    }
    if (formattedDuration.minutes > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("${formattedDuration.minutes}${context.getString(R.string.duration_minute)}")
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
