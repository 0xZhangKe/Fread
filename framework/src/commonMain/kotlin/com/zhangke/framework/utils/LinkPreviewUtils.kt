package com.zhangke.framework.utils

object LinkPreviewUtils {

    suspend fun fetchPreviewInfo(html: String): LinkPreviewInfo? {
        return null
    }
}

data class LinkPreviewInfo(
    val title: String,
    val description: String?,
    val image: String?,
    val url: String,
    val siteName: String?,
)
