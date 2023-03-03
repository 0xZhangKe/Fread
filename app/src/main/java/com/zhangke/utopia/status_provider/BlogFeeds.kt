package com.zhangke.utopia.status_provider

data class BlogFeeds(
    val id: Int,
    val name: String,
    val sourceList: List<StatusSource>
)

data class BlogFeedsShell(
    val id: Int,
    val name: String,
    val sourceIdList: List<String>
)