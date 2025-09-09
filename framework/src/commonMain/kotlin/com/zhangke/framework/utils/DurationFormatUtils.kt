package com.zhangke.framework.utils

import com.zhangke.fread.localization.LocalizedString
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
        builder.append("$weeks${getString(LocalizedString.durationWeek)}")
    }
    val days = (formattedDuration.days - weeks * 7).coerceAtLeast(0)
    if (days > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("$days${getString(LocalizedString.durationDay)}")
    }
    if (formattedDuration.hours > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("${formattedDuration.hours}${getString(LocalizedString.durationHour)}")
    }
    if (formattedDuration.minutes > 0) {
        if (builder.isNotEmpty()) {
            builder.append(" ")
        }
        builder.append("${formattedDuration.minutes}${getString(LocalizedString.durationMinute)}")
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
