package com.zhangke.utopia.common.utils

import com.zhangke.framework.utils.appContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun Duration.formattedString(): String {
    val context = appContext
    val builder = StringBuilder()
    var leftDuration = this
    val weeks = dayToWeek(leftDuration.inWholeDays)
    if (weeks > 0) {
        leftDuration -= (weeks * 7L).days
        builder.append("$weeks${context.getString(com.zhangke.utopia.framework.R.string.duration_week)}")
    }
    val days = leftDuration.inWholeDays
    if (days > 0) {
        leftDuration -= days.days
        builder.append("$days${context.getString(com.zhangke.utopia.framework.R.string.duration_day)}")
    }
    val hours = leftDuration.inWholeHours
    if (hours > 0) {
        leftDuration -= hours.hours
        builder.append("$hours${context.getString(com.zhangke.utopia.framework.R.string.duration_hour)}")
    }
    val minutes = leftDuration.inWholeMinutes
    if (minutes > 0) {
        leftDuration -= minutes.minutes
        builder.append("$minutes${context.getString(com.zhangke.utopia.framework.R.string.duration_minute)}")
    }
    return builder.toString()
}

fun dayToWeek(day: Long): Long {
    return day / 7
}
