package com.zhangke.fread.bluesky.internal.usecase

import app.bsky.feed.FeedViewPost
import app.bsky.feed.GetActorLikesQueryParams
import app.bsky.feed.GetAuthorFeedFilter
import app.bsky.feed.GetAuthorFeedQueryParams
import app.bsky.feed.GetFeedQueryParams
import app.bsky.feed.GetListFeedQueryParams
import app.bsky.feed.GetTimelineQueryParams
import app.bsky.feed.PostView
import app.bsky.feed.SearchPostsQueryParams
import app.bsky.feed.SearchPostsSort
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.model.BskyPagingFeeds
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Did
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
                    .convert(client.baseUrl)
            }

            is BlueskyFeeds.List -> {
                client.getListFeedCatching(
                    GetListFeedQueryParams(list = AtUri(feeds.uri), cursor = cursor)
                ).map { it.cursor to it.feed }
                    .convert(client.baseUrl)
            }

            is BlueskyFeeds.Following -> {
                client.getTimelineCatching(GetTimelineQueryParams(cursor = cursor))
                    .map { it.cursor to it.feed }
                    .convert(client.baseUrl)
            }

            is BlueskyFeeds.Hashtags -> {
                client.searchPostsCatching(
                    SearchPostsQueryParams(
                        q = feeds.hashtag,
                        sort = SearchPostsSort.TOP,
                        cursor = cursor,
                    )
                ).map { it.cursor to it.posts }.convertPostView(client.baseUrl)
            }

            is BlueskyFeeds.UserPosts -> {
                client.getAuthorFeedCatching(
                    GetAuthorFeedQueryParams(
                        actor = Did(feeds.did),
                        cursor = cursor,
                        includePins = true,
                        filter = GetAuthorFeedFilter.POSTS_AND_AUTHOR_THREADS,
                    )
                ).map { it.cursor to it.feed }.convert(client.baseUrl)
            }

            is BlueskyFeeds.UserReplies -> {
                client.getAuthorFeedCatching(
                    GetAuthorFeedQueryParams(
                        actor = Did(feeds.did),
                        cursor = cursor,
                        filter = GetAuthorFeedFilter.POSTS_WITH_REPLIES,
                    )
                ).map { it.cursor to it.feed }.convert(client.baseUrl)
            }

            is BlueskyFeeds.UserMedias -> {
                client.getAuthorFeedCatching(
                    GetAuthorFeedQueryParams(
                        actor = Did(feeds.did),
                        cursor = cursor,
                        filter = GetAuthorFeedFilter.POSTS_WITH_MEDIA,
                    )
                ).map { it.cursor to it.feed }.convert(client.baseUrl)
            }

            is BlueskyFeeds.UserLikes -> {
                client.getActorLikesCatching(
                    GetActorLikesQueryParams(
                        actor = Did(feeds.did),
                        cursor = cursor,
                    )
                ).map { it.cursor to it.feed }.convert(client.baseUrl)
            }
        }
    }

    private suspend fun Result<Pair<String?, ReadOnlyList<FeedViewPost>>>.convert(
        baseUrl: FormalBaseUrl
    ): Result<BskyPagingFeeds> {
        if (this.isFailure) return Result.failure(this.exceptionOrThrow())
        val (cursor, feeds) = this.getOrThrow()
        val status = feeds.map { buildBskyStatus(baseUrl, it) }
        return Result.success(BskyPagingFeeds(cursor, status))
    }

    private suspend fun Result<Pair<String?, ReadOnlyList<PostView>>>.convertPostView(
        baseUrl: FormalBaseUrl
    ): Result<BskyPagingFeeds> {
        if (this.isFailure) return Result.failure(this.exceptionOrThrow())
        val (cursor, feeds) = this.getOrThrow()
        val status = feeds.map { buildBskyStatus(baseUrl, it) }
        return Result.success(BskyPagingFeeds(cursor, status))
    }
}
