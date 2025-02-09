package com.zhangke.fread.common.status

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class StatusConfiguration(
    val loadFromServerLimit: Int,
    val loadFromLocalLimit: Int,
    val loadFromLocalRedundancies: Int,
    val autoFetchNewerFeedsInterval: Duration,
)

object StatusConfigurationDefault {

    val config = StatusConfiguration(
        loadFromServerLimit = 60,
        loadFromLocalLimit = 100,
        loadFromLocalRedundancies = 3,
        autoFetchNewerFeedsInterval = 2.minutes,
    )
}
