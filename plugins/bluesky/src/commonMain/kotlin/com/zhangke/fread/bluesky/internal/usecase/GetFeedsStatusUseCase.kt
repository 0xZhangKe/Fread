package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.feed.FeedViewPost
import app.bsky.feed.GetFeedQueryParams
import app.bsky.feed.GetListFeedQueryParams
import app.bsky.feed.GetTimelineQueryParams
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.model.BskyPagingFeeds
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.model.ReadOnlyList

class GetFeedsStatusUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val buildBskyStatus: BuildBskyStatusUseCase,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        feeds: BlueskyFeeds,
        cursor: String? = null,
    ): Result<BskyPagingFeeds> {
        val client = clientManager.getClient(role)
        return when (feeds) {
            is BlueskyFeeds.Feeds -> {
                client.getFeedCatching(GetFeedQueryParams(feed = AtUri(feeds.uri), cursor = cursor))
                    .map { it.cursor to it.feed }
                    .convertToPagingStatus(client.baseUrl)
            }

            is BlueskyFeeds.List -> {
                client.getListFeedCatching(
                    GetListFeedQueryParams(list = AtUri(feeds.uri), cursor = cursor)
                ).map { it.cursor to it.feed }
                    .convertToPagingStatus(client.baseUrl)
            }

            is BlueskyFeeds.Following -> {
                client.getTimelineCatching(GetTimelineQueryParams(cursor = cursor))
                    .map { it.cursor to it.feed }
                    .convertToPagingStatus(client.baseUrl)
            }
        }
    }

    private suspend fun Result<Pair<String?, ReadOnlyList<FeedViewPost>>>.convertToPagingStatus(
        baseUrl: FormalBaseUrl
    ): Result<BskyPagingFeeds> {
        if (this.isFailure) return Result.failure(this.exceptionOrThrow())
        val (cursor, feeds) = this.getOrThrow()
        val status = feeds.map { buildBskyStatus(baseUrl, it) }
        return Result.success(BskyPagingFeeds(cursor, status))
    }
}
