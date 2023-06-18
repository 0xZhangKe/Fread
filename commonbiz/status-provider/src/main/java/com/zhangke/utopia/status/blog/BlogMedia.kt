package com.zhangke.utopia.status.blog

data class BlogMedia(
    val url: String,
    val type: MediaType
)

enum class MediaType {

    IMAGE,
    VIDEO
}