package com.zhangke.utopia.common.status

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class StatusConfiguration(
    val loadFromServerLimit: Int,
    val loadFromLocalLimit: Int,
    val loadFromLocalRedundancies: Int,
    val autoFetchNewerFeedsInterval: Duration,
)

object StatusConfigurationDefault {

    val config = StatusConfiguration(
        loadFromServerLimit = 40,
        loadFromLocalLimit = 100,
        loadFromLocalRedundancies = 3,
        autoFetchNewerFeedsInterval = 60.seconds,
    )
}
