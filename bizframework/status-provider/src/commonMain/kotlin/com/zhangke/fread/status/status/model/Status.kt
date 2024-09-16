package com.zhangke.fread.status.status.model

import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.platform.BlogPlatform
import kotlinx.serialization.Serializable

@Serializable
sealed class Status: PlatformSerializable {

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

    /**
     * 触发该 Status 的用户
     */
    val triggerAuthor: BlogAuthor
        get() = when (this) {
            is NewBlog -> blog.author
            is Reblog -> author
        }

    val intrinsicBlog: Blog
        get() = when (this) {
            is NewBlog -> blog
            is Reblog -> reblog
        }

    @Serializable
    data class NewBlog(
        val blog: Blog,
        override val supportInteraction: List<StatusInteraction>,
    ) : Status(), PlatformSerializable {

        override val id: String get() = blog.id

        override val datetime: Long get() = blog.date.toEpochMilliseconds()

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
    ) : Status(), PlatformSerializable {

        override val platform: BlogPlatform
            get() = reblog.platform
    }
}
