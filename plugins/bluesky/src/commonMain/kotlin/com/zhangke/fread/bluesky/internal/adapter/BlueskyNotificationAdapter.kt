package com.zhangke.fread.bluesky.internal.adapter

import com.zhangke.fread.bluesky.internal.model.CompletedBskyNotification
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class BlueskyNotificationAdapter @Inject constructor(
    private val accountAdapter: BlueskyAccountAdapter,
    private val statusAdapter: BlueskyStatusAdapter,
) {

    fun convert(
        notification: CompletedBskyNotification,
        platform: BlogPlatform,
    ): StatusNotification {
        return notification.convert(platform)
    }

    private fun CompletedBskyNotification.convert(
        blogPlatform: BlogPlatform,
    ): StatusNotification {
        val author = accountAdapter.convertToBlogAuthor(this.author)
        return when (this.record) {
            is CompletedBskyNotification.Record.Like -> {
                StatusNotification.Like(
                    id = this.cid,
                    author = author,
                    blog = statusAdapter.convertToBlog(
                        post = this.record.post.record.bskyJson(),
                        id = this.record.post.cid.cid,
                        url = this.record.post.uri.atUri,
                        platform = blogPlatform,
                        author = author,
                        isSelfStatus = true,
                    ),
                    createAt = this.record.createAt,
                )
            }

            is CompletedBskyNotification.Record.Follow -> {
                StatusNotification.Follow(
                    id = this.cid,
                    author = author,
                    createAt = this.record.createAt,
                )
            }

            is CompletedBskyNotification.Record.Mention -> {
                StatusNotification.Mention(
                    author = author,
                    id = this.cid,
                    blog = statusAdapter.convertToBlog(
                        post = this.record.post,
                        id = this.record.cid,
                        url = this.record.uri,
                        platform = blogPlatform,
                        author = author,
                        isSelfStatus = false,
                    ),
                )
            }

            is CompletedBskyNotification.Record.Reply -> {
                StatusNotification.Reply(
                    id = this.cid,
                    author = author,
                    reply = statusAdapter.convertToBlog(
                        post = this.record.reply,
                        id = this.record.cid,
                        url = this.record.uri,
                        platform = blogPlatform,
                        author = author,
                        isSelfStatus = false,
                    ),
                )
            }

            is CompletedBskyNotification.Record.Quote -> {
                StatusNotification.Quote(
                    id = this.cid,
                    author = author,
                    quote = statusAdapter.convertToBlog(
                        post = this.record.quote,
                        id = this.record.cid,
                        url = this.record.uri,
                        platform = blogPlatform,
                        author = author,
                        isSelfStatus = false,
                    ),
                    blog = statusAdapter.convertToBlog(
                        post = this.record.post.record.bskyJson(),
                        id = this.record.post.cid.cid,
                        url = this.record.post.uri.atUri,
                        platform = blogPlatform,
                        author = author,
                        isSelfStatus = true,
                    ),
                )
            }

            is CompletedBskyNotification.Record.Repost -> {
                StatusNotification.Repost(
                    id = this.cid,
                    author = author,
                    blog = statusAdapter.convertToBlog(
                        post = this.record.post.record.bskyJson(),
                        id = this.record.post.cid.cid,
                        url = this.record.post.uri.atUri,
                        platform = blogPlatform,
                        author = author,
                        isSelfStatus = true,
                    ),
                    createAt = this.record.createAt,
                )
            }

            is CompletedBskyNotification.Record.OnlyMessage -> {
                StatusNotification.Unknown(
                    id = this.cid,
                    message = this.record.message,
                    createAt = this.record.createAt,
                )
            }
        }
    }
}
