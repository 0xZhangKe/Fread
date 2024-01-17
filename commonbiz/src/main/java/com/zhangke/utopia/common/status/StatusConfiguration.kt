package com.zhangke.utopia.common.status

data class StatusConfiguration(
    val loadFromServerLimit: Int,
    val loadFromLocalLimit: Int,
    val loadFromLocalRedundancies: Int,
)

object StatusConfigurationDefault {

    val config = StatusConfiguration(
        loadFromServerLimit = 40,
        loadFromLocalLimit = 100,
        loadFromLocalRedundancies = 3,
    )
}
