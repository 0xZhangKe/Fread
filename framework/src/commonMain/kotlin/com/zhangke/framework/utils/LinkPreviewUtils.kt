package com.zhangke.framework.utils

object LinkPreviewUtils {

    suspend fun fetchPreviewInfo(html: String): Result<LinkPreviewInfo> {
        return Result.failure(NotImplementedError("Link preview is not implemented yet"))
    }
}

data class LinkPreviewInfo(
    val title: String,
    val description: String?,
    val image: String?,
    val url: String,
    val siteName: String?,
)
