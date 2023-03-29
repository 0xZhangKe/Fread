package com.zhangke.utopia.status

data class BlogMedia(
    val url: String,
    val type: MediaType
)

enum class MediaType {

    IMAGE,
    VIDEO
}