package com.zhangke.utopia.status

data class BlogServer(
    val baseUrl: String,
    val name: String,
    val description: String,
    val avatar: String?,
    val protocol: String,
)