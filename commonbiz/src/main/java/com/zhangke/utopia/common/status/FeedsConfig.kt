package com.zhangke.utopia.common.status

import com.zhangke.utopia.status.uri.StatusProviderUri

data class FeedsConfig(
    val id: Long,
    val name: String,
    val sourceUriList: List<StatusProviderUri>,
)
