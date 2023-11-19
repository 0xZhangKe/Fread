package com.zhangke.utopia.status.status

import com.zhangke.utopia.status.blog.Blog

/**
 * Created by ZhangKe on 2022/12/4.
 */
sealed interface Status {

    val datetime: Long

    val authId: String

    val id: String

    data class NewBlog(
        val blog: Blog,
    ) : Status {

        override val id: String = blog.id

        override val datetime: Long = blog.date.time

        override val authId: String = blog.author.uri.toString()
    }
}
