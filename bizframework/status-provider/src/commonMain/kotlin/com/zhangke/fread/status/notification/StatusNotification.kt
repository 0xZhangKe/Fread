package com.zhangke.fread.status.notification

import com.zhangke.framework.datetime.Instant
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.StatusUiState
import kotlinx.serialization.Serializable

@Serializable
data class PagedStatusNotification(
    val notifications: List<StatusNotification>,
    val cursor: String?,
)

@Serializable
sealed interface StatusNotification {

    val id: String

    val createAt: Instant

    val status: StatusUiState?

    val unread: Boolean

    @Serializable
    data class Like(
        override val id: String,
        val author: BlogAuthor,
        val blog: Blog,
        override val createAt: Instant,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null
    }

    @Serializable
    data class Follow(
        override val id: String,
        val author: BlogAuthor,
        override val createAt: Instant,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null
    }

    @Serializable
    data class Mention(
        override val id: String,
        val author: BlogAuthor,
        override val status: StatusUiState,
        override val unread: Boolean,
    ) : StatusNotification {

        override val createAt: Instant
            get() = status.status.createAt
    }

    @Serializable
    data class Repost(
        override val id: String,
        val author: BlogAuthor,
        val blog: Blog,
        override val createAt: Instant,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null
    }

    @Serializable
    data class Quote(
        override val id: String,
        val author: BlogAuthor,
        val quote: StatusUiState,
        override val unread: Boolean,
    ) : StatusNotification {

        override val createAt: Instant
            get() = quote.status.createAt

        override val status: StatusUiState get() = quote
    }

    @Serializable
    data class Reply(
        override val id: String,
        val author: BlogAuthor,
        val reply: StatusUiState,
        override val unread: Boolean,
    ) : StatusNotification {

        override val createAt: Instant
            get() = reply.status.createAt

        override val status: StatusUiState get() = reply
    }

    @Serializable
    data class NewStatus(
        override val status: StatusUiState,
        override val unread: Boolean,
    ) : StatusNotification {
        override val id: String
            get() = status.status.id

        override val createAt: Instant
            get() = status.status.createAt
    }

    @Serializable
    data class FollowRequest(
        override val id: String,
        override val createAt: Instant,
        val author: BlogAuthor,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState?
            get() = null
    }

    @Serializable
    data class Poll(
        override val id: String,
        override val createAt: Instant,
        val blog: Blog,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null
    }

    @Serializable
    data class Update(
        override val id: String,
        override val createAt: Instant,
        override val status: StatusUiState,
        override val unread: Boolean,
    ) : StatusNotification

    @Serializable
    data class SeveredRelationships(
        override val id: String,
        override val createAt: Instant,
        val reason: String,
        override val unread: Boolean,
    ) : StatusNotification {
        override val status: StatusUiState? = null
    }

    @Serializable
    data class Unknown(
        override val id: String,
        val message: String,
        override val createAt: Instant,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null
    }
}
