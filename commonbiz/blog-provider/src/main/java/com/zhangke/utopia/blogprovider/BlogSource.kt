package com.zhangke.utopia.blogprovider

data class BlogSource(
    val sourceServer: String,
    val protocol: String,
    val sourceName: String?,
    val sourceDescription: String?,
    val avatar: String?,
)