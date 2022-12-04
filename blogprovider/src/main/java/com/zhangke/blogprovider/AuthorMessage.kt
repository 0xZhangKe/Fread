package com.zhangke.blogprovider

import java.util.*

/**
 * Created by ZhangKe on 2022/12/4.
 */

sealed class AuthorMessage {

    data class Blog(
        val author: BlogAuthor,
        val title: String?,
        val content: String,
        val mediaList: List<BlogMedia>?,
        val date: Date,
        val forwardCount: Int?,
        val likeCount: Int?
    ) : AuthorMessage()

    data class Forward(
        val author: BlogAuthor,
        val forwardComment: String?,
        val source: Forward?,
        val blog: Blog
    ) : AuthorMessage()
}