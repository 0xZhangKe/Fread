package com.zhangke.utopia.common.status

import com.zhangke.utopia.status.uri.FormalUri

data class FeedsConfig(
    val id: Long,
    val name: String,
    val sourceUriList: List<FormalUri>,
    val usageServerBaseUrl: String,
    val lastReadStatusId: String?,
)
