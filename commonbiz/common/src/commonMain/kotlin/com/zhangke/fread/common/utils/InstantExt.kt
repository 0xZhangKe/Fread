@file:OptIn(ExperimentalTime::class)

package com.zhangke.fread.common.utils

import com.zhangke.framework.date.InstantFormater
import kotlinx.datetime.Instant
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun getCurrentInstant(): Instant {
    return Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())
}

fun getCurrentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}

fun com.zhangke.framework.datetime.Instant.formatDefault(): String {
    return this.instant.formatDefault()
}

fun com.zhangke.framework.datetime.Instant.formatDate(): String {
    return this.instant.formatDate()
}

fun Instant.formatDefault(): String {
    return InstantFormater().formatToMediumDate(this)
}

fun Instant.formatDate(): String {
    return InstantFormater().formatToMediumDateWithoutTime(this)
}
