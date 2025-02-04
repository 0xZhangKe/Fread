package com.zhangke.fread.common.utils

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char

fun getCurrentInstant(): Instant {
    return Clock.System.now()
}

fun getCurrentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

fun com.zhangke.framework.datetime.Instant.formatDefault(): String {
    return instant.formatDefault()
}

fun Instant.formatDefault(): String {
    return format(
        DateTimeComponents.Format {
            year()
            char('-')
            monthNumber()
            char('-')
            dayOfMonth()
            char(' ')
            hour()
            char(':')
            minute()
            char(':')
            second()
        }
    )
}
