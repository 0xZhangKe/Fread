package com.zhangke.utopia.common.feeds.model

data class Feeds(
    val id: Int,
    val name: String,
    val sourceUriList: List<String>,
)
