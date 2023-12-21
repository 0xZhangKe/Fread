package com.zhangke.utopia.common.status

data class StatusConfiguration(
    val loadFromServerLimit: Int,
    val loadFromLocalRedundancies: Int,
)

object StatusConfigurationDefault {

    val config = StatusConfiguration(5, 3)
}
