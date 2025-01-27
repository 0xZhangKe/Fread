package com.zhangke.fread.status.notification

import com.zhangke.framework.datetime.Instant
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import kotlinx.serialization.Serializable

@Serializable
sealed interface StatusNotification {

    @Serializable
    data class Like(
        val author: BlogAuthor,
        val blog: Blog,
        val createAt: Instant,
    ) : StatusNotification

    @Serializable
    data class Follow(
        val author: BlogAuthor,
        val createAt: Instant,
    ) : StatusNotification

    @Serializable
    data class Mention(
        val author: BlogAuthor,
        val blog: Blog,
    ) : StatusNotification

    @Serializable
    data class Repost(
        val author: BlogAuthor,
        val blog: Blog,
        val createAt: Instant,
    ) : StatusNotification

    @Serializable
    data class Quote(
        val author: BlogAuthor,
        val quote: Blog,
        val blog: Blog,
    ) : StatusNotification

    @Serializable
    data class Reply(
        val author: BlogAuthor,
        val reply: Blog,
    ) : StatusNotification

    @Serializable
    data class Unknown(
        val message: String,
        val createAt: Instant,
    ) : StatusNotification
}
