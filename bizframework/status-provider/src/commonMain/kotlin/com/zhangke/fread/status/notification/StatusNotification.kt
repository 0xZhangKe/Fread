package com.zhangke.fread.status.notification

import com.zhangke.framework.datetime.Instant
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import kotlinx.serialization.Serializable

@Serializable
sealed interface StatusNotification {

    val id: String

    val createAt: Instant

    @Serializable
    data class Like(
        override val id: String,
        val author: BlogAuthor,
        val blog: Blog,
        override val createAt: Instant,
    ) : StatusNotification

    @Serializable
    data class Follow(
        override val id: String,
        val author: BlogAuthor,
        override val createAt: Instant,
    ) : StatusNotification

    @Serializable
    data class Mention(
        override val id: String,
        val author: BlogAuthor,
        val blog: Blog,
    ) : StatusNotification{

        override val createAt: Instant
            get() = blog.createAt
    }

    @Serializable
    data class Repost(
        override val id: String,
        val author: BlogAuthor,
        val blog: Blog,
        override val createAt: Instant,
    ) : StatusNotification

    @Serializable
    data class Quote(
        override val id: String,
        val author: BlogAuthor,
        val quote: Blog,
        val blog: Blog,
    ) : StatusNotification{

        override val createAt: Instant
            get() = blog.createAt
    }

    @Serializable
    data class Reply(
        override val id: String,
        val author: BlogAuthor,
        val reply: Blog,
    ) : StatusNotification{

        override val createAt: Instant
            get() = reply.createAt
    }

    @Serializable
    data class Unknown(
        override val id: String,
        val message: String,
        override val createAt: Instant,
    ) : StatusNotification
}
