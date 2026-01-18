package com.zhangke.framework.datetime

import com.zhangke.framework.serialize.TimestampAsInstantSerializer
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@Serializable(with = TimestampAsInstantSerializer::class)
data class Instant(val epochMillis: Long) {

    @OptIn(ExperimentalTime::class)
    val instant: kotlinx.datetime.Instant
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(epochMillis)
}

@OptIn(ExperimentalTime::class)
fun Instant(instant: kotlinx.datetime.Instant): Instant {
    return Instant(instant.toEpochMilliseconds())
}
