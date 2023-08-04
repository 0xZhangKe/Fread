package com.zhangke.utopia.status.status

import com.zhangke.framework.feeds.fetcher.StatusData
import com.zhangke.utopia.status.blog.Blog

/**
 * Created by ZhangKe on 2022/12/4.
 */
sealed class Status: StatusData {

    override val authorId: String
        get() = author.id

    data class NewBlog(
        val blog: Blog,
    ) : Status(blog.author, blog.supportedAction) {

        override val dataId: String
            get() = blog.id

        override val datetime: Long
            get() = blog.date.time
    }

    data class Forward(
        override val author: BlogAuthor,
        override val supportedAction: List<StatusAction>,
        val forwardComment: String?,
        val source: Forward?,
        val originBlog: Blog
    ) : Status(author, supportedAction) {

        override val dataId: String
            get() = TODO("Not yet implemented")

        override val datetime: Long
            get() = 0L
    }

    data class Comment(
        override val author: BlogAuthor,
        override val supportedAction: List<StatusAction>,
        val originBlog: Blog
    ) : Status(author, supportedAction) {

        override val datetime: Long
            get() = 0L

        override val dataId: String
            get() = TODO("Not yet implemented")
    }
}