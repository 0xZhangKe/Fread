package com.zhangke.framework.datetime

import cafe.adriel.voyager.core.lifecycle.JavaSerializable
import com.zhangke.framework.serialize.TimestampAsInstantSerializer
import kotlinx.serialization.Serializable

@Serializable(with = TimestampAsInstantSerializer::class)
data class Instant(val epochMillis: Long) : JavaSerializable {

    val instant: kotlinx.datetime.Instant
        get() = kotlinx.datetime.Instant.fromEpochMilliseconds(epochMillis)
}

fun Instant(instant: kotlinx.datetime.Instant): Instant {
    return Instant(instant.toEpochMilliseconds())
}
