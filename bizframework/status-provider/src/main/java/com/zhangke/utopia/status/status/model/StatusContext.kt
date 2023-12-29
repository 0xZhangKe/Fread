package com.zhangke.utopia.status.status.model

data class StatusContext(
    val ancestors: List<Status>,
    val descendants: List<Status>,
)
