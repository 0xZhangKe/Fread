package com.zhangke.fread.bluesky

import app.bsky.actor.GetProfileQueryParams
import app.bsky.feed.GetAuthorFeedFilter
import app.bsky.feed.GetAuthorFeedQueryParams
import app.bsky.feed.GetPostsQueryParams
import com.zhangke.fread.bluesky.internal.adapter.BlueskyStatusAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.repo.BlueskyPlatformRepo
import com.zhangke.fread.bluesky.internal.uri.user.UserUriTransformer
import com.zhangke.fread.bluesky.internal.usecase.BskyStatusInteractiveUseCase
import com.zhangke.fread.bluesky.internal.usecase.GetStatusContextUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdateRelationshipType
import com.zhangke.fread.bluesky.internal.usecase.UpdateRelationshipUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.PagedData
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.notBluesky
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.IStatusResolver
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import com.zhangke.fread.status.uri.FormalUri
import sh.christian.ozone.api.AtUri
import sh.christian.ozone.api.Did

class BlueskyStatusResolver(
    private val clientManager: BlueskyClientManager,
    private val statusAdapter: BlueskyStatusAdapter,
    private val platformRepo: BlueskyPlatformRepo,
    private val uriTransformer: UserUriTransformer,
    private val updateRelationship: UpdateRelationshipUseCase,
    private val statusInteractive: BskyStatusInteractiveUseCase,
    private val getStatusContextFunction: GetStatusContextUseCase,
    private val userUriTransformer: UserUriTransformer,
) : IStatusResolver {

    override suspend fun getStatus(
        locator: PlatformLocator,
        blogId: String?,
        blogUri: String?,
        platform: BlogPlatform
    ): Result<StatusUiState>? {
        if (platform.protocol.notBluesky) return null
        val client = clientManager.getClient(locator)
        val account = client.loggedAccountProvider()
        if (blogUri.isNullOrEmpty()) return Result.failure(IllegalArgumentException("blogUri is null!"))
        return client.getPostsCatching(GetPostsQueryParams(listOf(AtUri(blogUri))))
            .map {
                statusAdapter.convertToUiState(
                    locator = locator,
                    postView = it.posts.first(),
                    platform = platform,
                    loggedAccount = account,
                )
            }
    }

    override suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        maxId: String?,
    ): Result<PagedData<StatusUiState>>? {
        val uriInsight = uriTransformer.parse(uri) ?: return null
        val platform = platformRepo.getAllPlatform().first()
        val locator = PlatformLocator(baseUrl = platform.baseUrl)
        val client = clientManager.getClient(locator)
        val account = client.loggedAccountProvider()
        return client.getAuthorFeedCatching(
            GetAuthorFeedQueryParams(
                actor = Did(uriInsight.did),
                filter = GetAuthorFeedFilter.PostsAndAuthorThreads,
                includePins = true,
                limit = 80,
            )
        ).map { result ->
            PagedData(
                list = result.feed.map {
                    statusAdapter.convertToUiState(
                        locator = locator,
                        feedViewPost = it,
                        platform = platform,
                        loggedAccount = account,
                    )
                },
                cursor = result.cursor,
            )
        }
    }

    override suspend fun interactive(
        locator: PlatformLocator,
        status: Status,
        type: StatusActionType,
    ): Result<Status?>? {
        if (status.platform.protocol.notBluesky) return null
        return statusInteractive(locator, status, type)
    }

    override suspend fun votePoll(
        locator: PlatformLocator,
        blog: Blog,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        return null
    }

    override suspend fun getStatusContext(
        locator: PlatformLocator,
        status: Status
    ): Result<StatusContext>? {
        if (status.platform.protocol.notBluesky) return null
        return getStatusContextFunction(locator, status)
    }

    override suspend fun follow(
        locator: PlatformLocator,
        target: BlogAuthor
    ): Result<Unit>? {
        val userUriInsights = userUriTransformer.parse(target.uri) ?: return null
        return updateRelationship(
            locator = locator,
            targetDid = userUriInsights.did,
            type = UpdateRelationshipType.FOLLOW,
        ).map { }
    }

    override suspend fun unfollow(
        locator: PlatformLocator,
        target: BlogAuthor
    ): Result<Unit>? {
        val userUriInsights = userUriTransformer.parse(target.uri) ?: return null
        return updateRelationship(
            locator = locator,
            targetDid = userUriInsights.did,
            type = UpdateRelationshipType.UNFOLLOW,
        ).map { }
    }

    override suspend fun isFollowing(
        locator: PlatformLocator,
        target: BlogAuthor
    ): Result<Boolean>? {
        val did = userUriTransformer.parse(target.uri)?.did ?: return null
        val client = clientManager.getClient(locator)
        val profileResult = client.getProfileCatching(GetProfileQueryParams(Did(did)))
        if (profileResult.isFailure) return Result.failure(profileResult.exceptionOrNull()!!)
        val profile = profileResult.getOrThrow()
        return Result.success(profile.viewer?.following?.atUri.isNullOrEmpty().not())
    }

    override suspend fun translate(
        locator: PlatformLocator,
        status: Status,
        lan: String
    ): Result<BlogTranslation>? {
        if (status.platform.protocol.notBluesky) return null
        return Result.failure(RuntimeException("Not implemented"))
    }
}
