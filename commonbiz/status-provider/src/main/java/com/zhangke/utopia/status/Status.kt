package com.zhangke.utopia.status

import com.zhangke.framework.feeds.fetcher.StatusData

/**
 * Created by ZhangKe on 2022/12/4.
 */
sealed class Status(
    open val author: BlogAuthor,
    open val supportedAction: List<StatusAction>
): StatusData {

    override val authorId: String
        get() = author.id

    data class NewBlog(
        val blog: Blog,
    ) : Status(blog.author, blog.supportedAction){
        override val datetime: Long
            get() = blog.date.time
    }

    data class Forward(
        override val author: BlogAuthor,
        override val supportedAction: List<StatusAction>,
        val forwardComment: String?,
        val source: Forward?,
        val originBlog: Blog
    ) : Status(author, supportedAction){

        override val datetime: Long
            get() = 0L
    }

    data class Comment(
        override val author: BlogAuthor,
        override val supportedAction: List<StatusAction>,
        val originBlog: Blog
    ) : Status(author, supportedAction){

        override val datetime: Long
            get() = 0L
    }
}