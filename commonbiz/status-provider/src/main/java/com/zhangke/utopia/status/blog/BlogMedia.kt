package com.zhangke.utopia.status.blog

data class BlogMedia(
    val id: String,
    val url: String,
    val type: BlogMediaType,
    val previewUrl: String?,
    val remoteUrl: String?,
    val description: String?,
    val blurhash: String?,
    val meta: BlogMediaMeta?,
)
