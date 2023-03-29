package com.zhangke.utopia.status

/**
 * Created by ZhangKe on 2022/12/4.
 */
sealed class Status(
    open val author: BlogAuthor,
    open val supportedAction: List<StatusAction>
) {

    data class NewBlog(
        val blog: Blog
    ) : Status(blog.author, blog.supportedAction)

    data class Forward(
        override val author: BlogAuthor,
        override val supportedAction: List<StatusAction>,
        val forwardComment: String?,
        val source: Forward?,
        val originBlog: Blog
    ) : Status(author, supportedAction)

    data class Comment(
        override val author: BlogAuthor,
        override val supportedAction: List<StatusAction>,
        val originBlog: Blog
    ) : Status(author, supportedAction)
}