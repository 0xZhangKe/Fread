package com.zhangke.fread.common.utils

import com.zhangke.framework.date.InstantFormater
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

fun getCurrentInstant(): Instant {
    return Clock.System.now()
}

fun getCurrentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

fun com.zhangke.framework.datetime.Instant.formatDefault(): String {
    return this.instant.formatDefault()
}

fun Instant.formatDefault(): String {
    return InstantFormater().formatToMediumDate(this)
}
