package com.zhangke.utopia.rss.internal.rss

data class RssImage(
    val title: String?,
    val url: String?,
    val link: String?,
    val description: String?
) {
    fun isNotEmpty(): Boolean {
        return !url.isNullOrBlank() || !link.isNullOrBlank()
    }
}
