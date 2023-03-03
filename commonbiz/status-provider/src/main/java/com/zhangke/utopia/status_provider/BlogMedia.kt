package com.zhangke.utopia.status_provider

data class BlogMedia(
    val url: String,
    val type: MediaType
)

enum class MediaType {

    IMAGE,
    VIDEO
}