package com.zhangke.utopia.status.status.model

import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import kotlinx.serialization.Serializable

@Serializable
sealed class Status {

    abstract val datetime: Long

    /**
     * identify of this status, must non-empty
     */
    abstract val id: String

    @Serializable
    data class NewBlog(
        val blog: Blog,
    ) : Status() {

        override val id: String get() = blog.id

        override val datetime: Long get() = blog.date.time
    }

    @Serializable
    data class Reblog(
        val author: BlogAuthor,
        override val id: String,
        override val datetime: Long,
        val reblog: Blog,
    ) : Status()
}
