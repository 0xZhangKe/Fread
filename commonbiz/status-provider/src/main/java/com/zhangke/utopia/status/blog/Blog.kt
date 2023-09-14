package com.zhangke.utopia.status.blog

import com.zhangke.utopia.status.user.UtopiaUser
import java.util.Date

data class Blog(
    val id: String,
    val author: UtopiaUser,
    val title: String?,
    val content: String,
    val date: Date,
    val forwardCount: Int?,
    val likeCount: Int?,
    val repliesCount: Int?,
    val sensitive: Boolean,
    val spoilerText: String,
    val mediaList: List<BlogMedia>,
    val poll: BlogPoll?,
)
