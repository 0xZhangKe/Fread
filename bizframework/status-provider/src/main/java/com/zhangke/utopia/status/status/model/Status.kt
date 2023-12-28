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

    abstract val platform: BlogPlatform

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
    ) : Status(){

        override val platform: BlogPlatform
            get() = reblog.platform
    }
}
