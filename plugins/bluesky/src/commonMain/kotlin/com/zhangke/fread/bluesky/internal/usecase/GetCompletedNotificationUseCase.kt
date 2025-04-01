package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.feed.GetPostsQueryParams
import app.bsky.feed.Like
import app.bsky.feed.PostView
import app.bsky.feed.Repost
import app.bsky.graph.Follow
import app.bsky.notification.ListNotificationsNotification
import app.bsky.notification.ListNotificationsQueryParams
import app.bsky.notification.ListNotificationsReason
import com.zhangke.framework.datetime.Instant
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.client.BlueskyClient
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.CompletedBskyNotification
import com.zhangke.fread.bluesky.internal.model.PagedCompletedBskyNotifications
import com.zhangke.fread.bluesky.internal.utils.bskyJson
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri

class GetCompletedNotificationUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        params: ListNotificationsQueryParams,
    ): Result<PagedCompletedBskyNotifications> {
        val client = clientManager.getClient(role)
        val loggedAccount = client.loggedAccountProvider()
        val notificationSerializedCache = mutableMapOf<ListNotificationsNotification, Any>()
        return client.listNotificationsCatching(params)
            .mapCatching { notification ->
                val uriList =
                    notification.notifications.getNeedFetchPostUris(notificationSerializedCache)
                val postList = if (uriList.isNotEmpty()) {
                    fetchPostByUris(client, uriList).getOrThrow()
                } else {
                    null
                }
                PagedCompletedBskyNotifications(
                    cursor = notification.cursor,
                    notifications = notification.notifications.mapNotNull {
                        it.convert(postList, notificationSerializedCache, loggedAccount)
                    },
                    priority = notification.priority,
                    seenAt = notification.seenAt?.let { Instant(it) },
                )
            }
    }

    private fun ListNotificationsNotification.convert(
        posList: List<PostView>?,
        serializedCache: MutableMap<ListNotificationsNotification, Any>,
        loggedAccount: BlueskyLoggedAccount?,
    ): CompletedBskyNotification? {
        val isOwner = loggedAccount?.did == author.did.did
        val record: CompletedBskyNotification.Record = when (reason) {
            ListNotificationsReason.Like -> {
                val like: Like = (serializedCache[this] as? Like) ?: record.bskyJson()
                CompletedBskyNotification.Record.Like(
                    post = posList!!.firstOrNull { it.uri == like.subject.uri } ?: return null,
                    createAt = Instant(like.createdAt),
                )
            }

            ListNotificationsReason.Repost -> {
                val repost: Repost = (serializedCache[this] as? Repost) ?: record.bskyJson()
                CompletedBskyNotification.Record.Repost(
                    post = posList!!.firstOrNull { it.uri == repost.subject.uri } ?: return null,
                    createAt = Instant(repost.createdAt),
                )
            }

            ListNotificationsReason.Follow -> {
                val follow: Follow = this.record.bskyJson()
                CompletedBskyNotification.Record.Follow(
                    createAt = Instant(follow.createdAt),
                )
            }

            ListNotificationsReason.Mention -> {
                CompletedBskyNotification.Record.Mention(
                    post = this.record.bskyJson(),
                    cid = this.cid.cid,
                    uri = this.uri.atUri,
                    isOwner = isOwner,
                )
            }

            ListNotificationsReason.Reply -> {
                CompletedBskyNotification.Record.Reply(
                    reply = this.record.bskyJson(),
                    cid = this.cid.cid,
                    uri = this.uri.atUri,
                    isOwner = isOwner,
                )
            }

            ListNotificationsReason.Quote -> {
                CompletedBskyNotification.Record.Quote(
                    quote = this.record.bskyJson(),
                    cid = this.cid.cid,
                    uri = this.uri.atUri,
                    post = posList!!.firstOrNull { it.uri == this.reasonSubject!! } ?: return null,
                    isOwner = isOwner,
                )
            }

            ListNotificationsReason.StarterpackJoined -> {
                CompletedBskyNotification.Record.OnlyMessage(
                    message = "StarterackJoined: ${this.record}",
                    createAt = Instant(this.indexedAt),
                )
            }

            is ListNotificationsReason.Unknown -> {
                CompletedBskyNotification.Record.OnlyMessage(
                    message = "Unknown(${(reason as? ListNotificationsReason.Unknown)?.rawValue}): ${this.record}",
                    createAt = Instant(this.indexedAt),
                )
            }
        }
        return CompletedBskyNotification(
            uri = this.uri.atUri,
            cid = this.cid.cid,
            record = record,
            author = this.author,
            isRead = this.isRead,
            indexedAt = Instant(this.indexedAt),
            labels = this.labels,
        )
    }

    private suspend fun fetchPostByUris(
        client: BlueskyClient,
        uriList: List<AtUri>,
    ): Result<List<PostView>> {
        if (uriList.isEmpty()) return Result.success(emptyList())
        val resultList: List<Result<List<PostView>>> = supervisorScope {
            val grouped = uriList.chunked(15)
            grouped.map { itemList ->
                async { client.getPostsCatching(GetPostsQueryParams(uris = itemList)) }
            }.awaitAll().map { result -> result.map { it.posts } }
        }
        val error = resultList.firstOrNull { it.isFailure }
        if (error != null) {
            return error
        }
        return Result.success(resultList.flatMap { it.getOrThrow() })
    }

    private fun List<ListNotificationsNotification>.getNeedFetchPostUris(
        notificationSerializedCache: MutableMap<ListNotificationsNotification, Any>,
    ): List<AtUri> {
        return mapNotNull {
            when (it.reason) {
                is ListNotificationsReason.Repost -> {
                    val repost: Repost = it.record.bskyJson()
                    notificationSerializedCache[it] = repost
                    repost.subject.uri
                }

                is ListNotificationsReason.Like -> {
                    val like: Like = it.record.bskyJson()
                    notificationSerializedCache[it] = like
                    like.subject.uri
                }

                is ListNotificationsReason.Quote -> {
                    it.reasonSubject!!
                }

                else -> null
            }
        }
    }
}
