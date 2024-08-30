package com.zhangke.fread.common.ext

import kotlinx.datetime.Instant
import java.util.Date

fun Instant.toJavaDate(): Date {
    return Date(toEpochMilliseconds())
}
