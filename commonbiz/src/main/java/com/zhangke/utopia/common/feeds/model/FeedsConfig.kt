package com.zhangke.utopia.common.feeds.model

data class FeedsConfig(
    val id: Long,
    val authorUserId: String,
    val name: String,
    val sourceUriList: List<String>,
    val databaseFilePath: String?,
)
