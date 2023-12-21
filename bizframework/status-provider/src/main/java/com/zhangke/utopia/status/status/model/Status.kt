package com.zhangke.utopia.status.status.model

import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog

/**
 * Created by ZhangKe on 2022/12/4.
 */
sealed interface Status {

    val datetime: Long

    /**
     * identify of this status, must non-empty
     */
    val id: String

    data class NewBlog(
        val blog: Blog,
    ) : Status {

        override val id: String get() = blog.id

        override val datetime: Long get() = blog.date.time
    }

    data class Reblog(
        val author: BlogAuthor,
        override val id: String,
        override val datetime: Long,
        val reblog: Blog,
    ) : Status
}
