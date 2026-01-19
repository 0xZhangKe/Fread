package com.zhangke.fread.bluesky.internal.adapter

import com.zhangke.fread.bluesky.internal.model.CompletedBskyNotification
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status

class BlueskyNotificationAdapter(
    private val accountAdapter: BlueskyAccountAdapter,
    private val statusAdapter: BlueskyStatusAdapter,
) {

    fun convert(
        notification: CompletedBskyNotification,
        locator: PlatformLocator,
        platform: BlogPlatform,
    ): StatusNotification {
        return notification.convertNotification(locator, platform)
    }

    private fun CompletedBskyNotification.convertNotification(
        locator: PlatformLocator,
        blogPlatform: BlogPlatform,
    ): StatusNotification {
        val author = accountAdapter.convertToBlogAuthor(this.author)
        return when (this.record) {
            is CompletedBskyNotification.Record.Like -> {
                StatusNotification.Like(
                    id = this.cid,
                    locator = locator,
                    author = author,
                    unread = !this.isRead,
                    blog = statusAdapter.convertToBlog(
                        post = this.record.post.record.bskyJson(),
                        id = this.record.post.cid.cid,
                        url = this.record.post.uri.atUri,
                        platform = blogPlatform,
                        author = accountAdapter.convertToBlogAuthor(this.record.post.author),
                    ),
                    createAt = this.record.createAt,
                )
            }

            is CompletedBskyNotification.Record.Follow -> {
                StatusNotification.Follow(
                    id = this.cid,
                    author = author,
                    locator = locator,
                    unread = !this.isRead,
                    createAt = this.record.createAt,
                )
            }

            is CompletedBskyNotification.Record.Mention -> {
                StatusNotification.Mention(
                    author = author,
                    id = this.cid,
                    unread = !this.isRead,
                    status = statusAdapter.convertToUiState(
                        locator = locator,
                        status = Status.NewBlog(
                            statusAdapter.convertToBlog(
                                post = this.record.post,
                                id = this.record.cid,
                                url = this.record.uri,
                                platform = blogPlatform,
                                author = author,
                            )
                        ),
                        logged = true,
                        isOwner = this.record.isOwner,
                    ),
                )
            }

            is CompletedBskyNotification.Record.Reply -> {
                StatusNotification.Reply(
                    id = this.cid,
                    author = author,
                    unread = !this.isRead,
                    reply = statusAdapter.convertToUiState(
                        locator = locator,
                        status = Status.NewBlog(
                            statusAdapter.convertToBlog(
                                post = this.record.reply,
                                id = this.record.cid,
                                url = this.record.uri,
                                platform = blogPlatform,
                                author = author,
                            )
                        ),
                        logged = true,
                        isOwner = this.record.isOwner,
                    ),
                )
            }

            is CompletedBskyNotification.Record.Quote -> {
                StatusNotification.Quote(
                    id = this.cid,
                    unread = !this.isRead,
                    author = author,
                    quote = statusAdapter.convertToUiState(
                        locator = locator,
                        status = Status.NewBlog(
                            statusAdapter.convertToBlog(
                                post = this.record.quote,
                                id = this.record.cid,
                                url = this.record.uri,
                                platform = blogPlatform,
                                author = author,
                            )
                        ),
                        logged = true,
                        isOwner = this.record.isOwner,
                    ),
                )
            }

            is CompletedBskyNotification.Record.Repost -> {
                StatusNotification.Repost(
                    id = this.cid,
                    author = author,
                    locator = locator,
                    unread = !this.isRead,
                    blog = statusAdapter.convertToBlog(
                        post = this.record.post.record.bskyJson(),
                        id = this.record.post.cid.cid,
                        url = this.record.post.uri.atUri,
                        platform = blogPlatform,
                        author = author,
                    ),
                    createAt = this.record.createAt,
                )
            }

            is CompletedBskyNotification.Record.OnlyMessage -> {
                StatusNotification.Unknown(
                    id = this.cid,
                    locator = locator,
                    unread = !this.isRead,
                    message = this.record.message,
                    createAt = this.record.createAt,
                )
            }
        }
    }
}
