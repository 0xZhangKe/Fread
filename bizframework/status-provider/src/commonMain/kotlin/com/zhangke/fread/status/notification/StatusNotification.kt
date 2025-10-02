package com.zhangke.fread.status.notification

import com.zhangke.framework.datetime.Instant
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.model.FormattingTime
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import kotlinx.serialization.Serializable

@Serializable
data class PagedStatusNotification(
    val notifications: List<StatusNotification>,
    val cursor: String?,
    val reachEnd: Boolean,
)

@Serializable
sealed interface StatusNotification {

    val id: String

    val createAt: Instant

    val locator: PlatformLocator

    val status: StatusUiState?

    val unread: Boolean

    val formattingDisplayTime: FormattingTime

    @Serializable
    data class Like(
        override val locator: PlatformLocator,
        override val id: String,
        val author: BlogAuthor,
        val blog: Blog,
        override val createAt: Instant,
        override val unread: Boolean,
    ) : StatusNotification {

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }

        override val status: StatusUiState? = null
    }

    @Serializable
    data class Follow(
        override val locator: PlatformLocator,
        override val id: String,
        val author: BlogAuthor,
        override val createAt: Instant,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
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

        override val locator: PlatformLocator
            get() = status.locator

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
    }

    @Serializable
    data class Repost(
        override val id: String,
        val author: BlogAuthor,
        val blog: Blog,
        override val locator: PlatformLocator,
        override val createAt: Instant,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
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

        override val locator: PlatformLocator
            get() = quote.locator

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
    }

    @Serializable
    data class QuoteUpdate(
        override val id: String,
        val author: BlogAuthor,
        val quote: StatusUiState,
        override val unread: Boolean,
        override val createAt: Instant,
    ) : StatusNotification {

        override val status: StatusUiState get() = quote

        override val locator: PlatformLocator
            get() = quote.locator

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
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

        override val locator: PlatformLocator
            get() = reply.locator

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
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

        override val locator: PlatformLocator
            get() = status.locator

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
    }

    @Serializable
    data class FollowRequest(
        override val id: String,
        override val locator: PlatformLocator,
        override val createAt: Instant,
        val author: BlogAuthor,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState?
            get() = null

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
    }

    @Serializable
    data class Poll(
        override val id: String,
        override val createAt: Instant,
        val blog: Blog,
        override val locator: PlatformLocator,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
    }

    @Serializable
    data class Update(
        override val id: String,
        override val createAt: Instant,
        override val status: StatusUiState,
        override val unread: Boolean,
    ) : StatusNotification {

        override val locator: PlatformLocator
            get() = status.locator

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
    }

    @Serializable
    data class SeveredRelationships(
        override val id: String,
        override val createAt: Instant,
        override val locator: PlatformLocator,
        val author: BlogAuthor,
        val reason: String,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
    }

    @Serializable
    data class Unknown(
        override val id: String,
        override val locator: PlatformLocator,
        val message: String,
        override val createAt: Instant,
        override val unread: Boolean,
    ) : StatusNotification {

        override val status: StatusUiState? = null

        override val formattingDisplayTime: FormattingTime by lazy {
            FormattingTime(createAt)
        }
    }
}
