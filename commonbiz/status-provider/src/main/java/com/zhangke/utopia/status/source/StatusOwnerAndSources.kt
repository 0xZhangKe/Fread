package com.zhangke.utopia.status.source

data class StatusOwnerAndSources(
    val owner: StatusSourceOwner,
    val sourceList: List<StatusSource>,
)
