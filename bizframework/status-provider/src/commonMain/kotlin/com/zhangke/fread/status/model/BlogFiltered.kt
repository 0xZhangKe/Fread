package com.zhangke.fread.status.model

import kotlinx.serialization.Serializable

@Serializable
data class BlogFiltered(
    val id: String,
    val title: String,
    val action: FilterAction,
    val keywordMatches: List<String>,
) {

    enum class FilterAction {
        WARN,
        HIDE,
        BLUR,
    }
}
