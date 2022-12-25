package com.zhangke.utopia.blogprovider

data class BlogMedia(
    val url: String,
    val type: MediaType
)

enum class MediaType {

    IMAGE,
    VIDEO
}