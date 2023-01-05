package com.zhangke.utopia.blogprovider

import java.util.*

data class Blog(
    val author: BlogAuthor,
    val supportedAction: List<StatusAction>,
    val title: String?,
    val content: String,
    val mediaList: List<BlogMedia>?,
    val date: Date,
    val forwardCount: Int?,
    val likeCount: Int?
)