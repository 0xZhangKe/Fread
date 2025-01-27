package com.zhangke.fread.bluesky.internal.screen.notification

import androidx.lifecycle.ViewModel
import app.bsky.notification.ListNotificationsQueryParams
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.bluesky.internal.adapter.BlueskyAccountAdapter
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.CompletedBskyNotification
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.usecase.GetCompletedNotificationUseCase
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.notification.StatusNotification
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject

class BskyNotificationViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val role: IdentityRole,
    private val platformRepo: BlueskyPlatformRepo,
    private val accountAdapter: BlueskyAccountAdapter,
    private val statusAdapter: BlueskyStatusAdapter,
    private val getCompletedNotification: GetCompletedNotificationUseCase,
    private val onlyMention: Boolean,
) : ViewModel() {

    private var cursor: String? = null

    private var blogPlatform: BlogPlatform? = null

    private suspend fun loadNotification(cursor: String? = this.cursor): Result<List<StatusNotification>> {
        val platform = getBlogPlatform(clientManager.getClient(role))
        return getCompletedNotification(
            role = role,
            params = ListNotificationsQueryParams(
                reasons = if (onlyMention) listOf("mention", "reply", "quote") else emptyList(),
                cursor = cursor,
            ),
        ).map { paged ->
            this@BskyNotificationViewModel.cursor = paged.cursor
            paged.notifications.map { it.convert(platform) }
        }
    }

    private fun CompletedBskyNotification.convert(
        blogPlatform: BlogPlatform,
    ): StatusNotification {
        val author = accountAdapter.convertToBlogAuthor(this.author)
        return when (this.record) {
            is CompletedBskyNotification.Record.Like -> {
                StatusNotification.Like(
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
                    author = author,
                    createAt = this.record.createAt,
                )
            }

            is CompletedBskyNotification.Record.Mention -> {
                StatusNotification.Mention(
                    author = author,
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
                    message = this.record.message,
                    createAt = this.record.createAt,
                )
            }
        }
    }

    private suspend fun getBlogPlatform(client: BlueskyClient): BlogPlatform {
        return blogPlatform ?: platformRepo.getPlatform(client.baseUrl).also { blogPlatform = it }
    }
}
