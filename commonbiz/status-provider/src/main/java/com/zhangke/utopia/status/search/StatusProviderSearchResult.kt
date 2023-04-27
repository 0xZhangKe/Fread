package com.zhangke.utopia.status.search

data class StatusProviderSearchResult(
    val uri: String,
    val type: StatusProviderSearchType,
    val name: String,
    val desc: String,
    val thumbnail: String?,
)

enum class StatusProviderSearchType{

    STATUS_SOURCE,
    STATUS_SOURCE_OWNER,
}
