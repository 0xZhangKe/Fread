package com.zhangke.utopia.status.status.model

import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.Blog
import com.zhangke.utopia.status.platform.BlogPlatform
import kotlinx.serialization.Serializable

@Serializable
sealed class Status {

    abstract val datetime: Long

    /**
     * identify of this status, must non-empty
     */
    abstract val id: String

    abstract val supportInteraction: List<StatusInteraction>

    /**
     * 该 Platform 表示获取到该 Status 时使用的 Platform，而不是 Status 本身所属的 Platform。
     */
    abstract val platform: BlogPlatform

    val intrinsicBlog: Blog get() = when (this) {
        is NewBlog -> blog
        is Reblog -> reblog
    }

    @Serializable
    data class NewBlog(
        val blog: Blog,
        override val supportInteraction: List<StatusInteraction>,
    ) : Status() {

        override val id: String get() = blog.id

        override val datetime: Long get() = blog.date.time

        override val platform: BlogPlatform
            get() = blog.platform
    }

    @Serializable
    data class Reblog(
        val author: BlogAuthor,
        override val id: String,
        override val datetime: Long,
        val reblog: Blog,
        override val supportInteraction: List<StatusInteraction>,
    ) : Status() {

        override val platform: BlogPlatform
            get() = reblog.platform
    }
}
