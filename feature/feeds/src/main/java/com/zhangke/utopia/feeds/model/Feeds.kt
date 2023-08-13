package com.zhangke.utopia.feeds.model

internal data class Feeds(
    val id: Int,
    val name: String,
    val sourceUriList: List<String>,
)
