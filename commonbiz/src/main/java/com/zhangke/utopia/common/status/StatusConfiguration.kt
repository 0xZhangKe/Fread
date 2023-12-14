package com.zhangke.utopia.common.status

data class StatusConfiguration(
    val loadFromServerLimit: Int,
)

object StatusConfigurationDefault {

    val config = StatusConfiguration(100)
}
