package com.zhangke.fread.bluesky.internal.model

import app.bsky.actor.ProfileView
import app.bsky.feed.Post
import app.bsky.feed.PostView
import com.atproto.label.Label
import com.zhangke.framework.datetime.Instant
import sh.christian.ozone.api.AtUri

data class PagedCompletedBskyNotifications(
    val cursor: String? = null,
    val notifications: List<CompletedBskyNotification>,
    val priority: Boolean? = null,
    val seenAt: Instant? = null,
)

data class CompletedBskyNotification(
    val uri: String,
    val cid: String,
    val author: ProfileView,
    val reasonSubject: AtUri? = null,
    val record: Record,
    val isRead: Boolean,
    val indexedAt: Instant,
    val labels: List<Label> = emptyList(),
) {

    sealed interface Record {

        data class Like(
            val post: PostView,
            val createAt: Instant,
        ) : Record

        data class Repost(
            val post: PostView,
            val createAt: Instant,
        ) : Record

        data class Follow(val createAt: Instant) : Record

        data class Mention(
            val post: Post,
            val cid: String,
            val uri: String,
        ) : Record

        data class Quote(
            val quote: Post,
            val cid: String,
            val uri: String,
            val post: PostView,
        ) : Record

        data class Reply(
            val reply: Post,
            val cid: String,
            val uri: String,
        ) : Record

        data class OnlyMessage(
            val message: String,
            val createAt: Instant,
        ) : Record
    }
}
