package com.zhangke.utopia.status.blog

import com.zhangke.utopia.status.status.StatusAction
import java.util.*

data class Blog(
    val id: String,
    val author: BlogAuthor,
    val supportedAction: List<StatusAction>,
    val title: String?,
    val content: String,
    val mediaList: List<BlogMedia>?,
    val date: Date,
    val forwardCount: Int?,
    val likeCount: Int?,
    val repliesCount: Int?,
)