package com.zhangke.utopia.blogprovider

import java.util.*

/**
 * Created by ZhangKe on 2022/12/4.
 */
sealed class Status(
    open val author: BlogAuthor,
    open val supportedAction: List<StatusAction>
) {

    data class NewBlog(
        override val author: BlogAuthor,
        override val supportedAction: List<StatusAction>,
        val title: String?,
        val content: String,
        val mediaList: List<BlogMedia>?,
        val date: Date,
        val forwardCount: Int?,
        val likeCount: Int?
    ) : Status(author, supportedAction)

    data class Forward(
        override val author: BlogAuthor,
        override val supportedAction: List<StatusAction>,
        val forwardComment: String?,
        val source: Forward?,
        val blog: NewBlog
    ) : Status(author, supportedAction)
}