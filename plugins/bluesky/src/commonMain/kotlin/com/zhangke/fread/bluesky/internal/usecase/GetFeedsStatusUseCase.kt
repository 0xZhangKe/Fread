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
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.account.BlueskyLoggedAccount
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.model.BskyPagingFeeds
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import me.tatarka.inject.annotations.Inject
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Did

class GetFeedsStatusUseCase @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val statusAdapter: BlueskyStatusAdapter,
    private val blogPlatformRepo: BlueskyPlatformRepo,
) {

    suspend operator fun invoke(
        locator: PlatformLocator,
        feeds: BlueskyFeeds,
        cursor: String? = null,
    ): Result<BskyPagingFeeds> {
        val client = clientManager.getClient(locator)
        val platform = blogPlatformRepo.getPlatform(client.baseUrl)
        val loggedAccount = client.loggedAccountProvider()
        return when (feeds) {
            is BlueskyFeeds.Feeds -> {
                client.getFeedCatching(GetFeedQueryParams(feed = AtUri(feeds.uri), cursor = cursor))
                    .map { it.cursor to it.feed }
                    .convert(locator, platform, loggedAccount)
            }

            is BlueskyFeeds.List -> {
                client.getListFeedCatching(
                    GetListFeedQueryParams(list = AtUri(feeds.uri), cursor = cursor)
                ).map { it.cursor to it.feed }
                    .convert(locator, platform, loggedAccount)
            }

            is BlueskyFeeds.FollowingTimeline -> {
                client.getTimelineCatching(GetTimelineQueryParams(cursor = cursor))
                    .map { it.cursor to it.feed }
                    .convert(locator, platform, loggedAccount)
            }

            is BlueskyFeeds.Hashtags -> {
                client.searchPostsCatching(
                    SearchPostsQueryParams(
                        q = feeds.hashtag,
                        sort = SearchPostsSort.Top,
                        cursor = cursor,
                    )
                ).map { it.cursor to it.posts }.convertPostView(locator, platform, loggedAccount)
            }

            is BlueskyFeeds.UserPosts -> {
                client.getAuthorFeedCatching(
                    GetAuthorFeedQueryParams(
                        actor = Did(feeds.did),
                        cursor = cursor,
                        includePins = true,
                        filter = GetAuthorFeedFilter.PostsAndAuthorThreads,
                    )
                ).map { it.cursor to it.feed }.convert(locator, platform, loggedAccount)
            }

            is BlueskyFeeds.UserReplies -> {
                client.getAuthorFeedCatching(
                    GetAuthorFeedQueryParams(
                        actor = Did(feeds.did),
                        cursor = cursor,
                        filter = GetAuthorFeedFilter.PostsWithReplies,
                    )
                ).map { it.cursor to it.feed }.convert(locator, platform, loggedAccount)
            }

            is BlueskyFeeds.UserMedias -> {
                val did = feeds.did ?: loggedAccount?.did ?: return Result.failure(
                    IllegalArgumentException("did is null")
                )
                client.getAuthorFeedCatching(
                    GetAuthorFeedQueryParams(
                        actor = Did(did),
                        cursor = cursor,
                        filter = GetAuthorFeedFilter.PostsWithMedia,
                    )
                ).map { it.cursor to it.feed }.convert(locator, platform, loggedAccount)
            }

            is BlueskyFeeds.UserLikes -> {
                val did = feeds.did ?: loggedAccount?.did ?: return Result.failure(
                    IllegalArgumentException("did is null")
                )
                client.getActorLikesCatching(
                    GetActorLikesQueryParams(
                        actor = Did(did),
                        cursor = cursor,
                    )
                ).map { it.cursor to it.feed }.convert(locator, platform, loggedAccount)
            }
        }
    }

    private fun Result<Pair<String?, List<FeedViewPost>>>.convert(
        locator: PlatformLocator,
        platform: BlogPlatform,
        loggedAccount: BlueskyLoggedAccount?,
    ): Result<BskyPagingFeeds> {
        if (this.isFailure) return Result.failure(this.exceptionOrThrow())
        val (cursor, feeds) = this.getOrThrow()
        val status = feeds.map {
            statusAdapter.convertToUiState(
                locator = locator,
                feedViewPost = it,
                platform = platform,
                loggedAccount = loggedAccount,
            )
        }
        return Result.success(BskyPagingFeeds(cursor, status))
    }

    private fun Result<Pair<String?, List<PostView>>>.convertPostView(
        locator: PlatformLocator,
        platform: BlogPlatform,
        loggedAccount: BlueskyLoggedAccount?,
    ): Result<BskyPagingFeeds> {
        if (this.isFailure) return Result.failure(this.exceptionOrThrow())
        val (cursor, feeds) = this.getOrThrow()
        val status = feeds.map {
            statusAdapter.convertToUiState(
                locator = locator,
                postView = it,
                platform = platform,
                loggedAccount = loggedAccount,
            )
        }
        return Result.success(BskyPagingFeeds(cursor, status))
    }
}
