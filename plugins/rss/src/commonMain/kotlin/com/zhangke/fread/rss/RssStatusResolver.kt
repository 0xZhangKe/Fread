package com.zhangke.fread.rss

import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.rss.internal.repo.RssStatusRepo
import com.zhangke.fread.rss.internal.uri.RssUriTransformer
import com.zhangke.fread.rss.internal.uri.isRssUri
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.PagedData
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.isRss
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.IStatusResolver
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import com.zhangke.fread.status.uri.FormalUri

class RssStatusResolver(
    private val uriTransformer: RssUriTransformer,
    private val rssStatusRepo: RssStatusRepo,
) : IStatusResolver {

    override suspend fun getStatus(
        locator: PlatformLocator,
        blogId: String?,
        blogUri: String?,
        platform: BlogPlatform
    ): Result<StatusUiState>? {
        return null
    }

    override suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        maxId: String?,
    ): Result<PagedData<StatusUiState>>? {
        if (!uri.isRssUri) return null
        val uriInsight = uriTransformer.parse(uri)
            ?: return Result.failure(IllegalArgumentException("Unknown uri: $uri"))
        if (!maxId.isNullOrEmpty()) return Result.success(PagedData(emptyList(), null))
        val fetchResult = rssStatusRepo.getStatus(uriInsight)
            .map { it.sortedByDescending { status -> status.createAt.epochMillis } }
        if (fetchResult.isFailure) return Result.failure(fetchResult.exceptionOrThrow())
        val baseUrl = FormalBaseUrl.parse(uriInsight.url) ?: return Result.failure(
            IllegalArgumentException("Invalid base URL: ${uriInsight.url}")
        )
        val locator = PlatformLocator(baseUrl = baseUrl)
        val finalReturnItems = fetchResult.getOrThrow().map {
            StatusUiState(
                locator = locator,
                status = it,
                logged = false,
                isOwner = false,
                blogTranslationState = BlogTranslationUiState(false),
            )
        }
        if (finalReturnItems.isEmpty()) return Result.success(PagedData(emptyList(), null))
        return Result.success(PagedData(finalReturnItems, null))
    }

    override suspend fun interactive(
        locator: PlatformLocator,
        status: Status,
        type: StatusActionType,
    ): Result<Status?>? {
        return null
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
        status: Status,
    ): Result<StatusContext>? {
        if (!status.platform.protocol.isRss) return null
        return Result.success(
            StatusContext(
                ancestors = emptyList(),
                status = null,
                descendants = emptyList(),
            )
        )
    }

    override suspend fun follow(locator: PlatformLocator, target: BlogAuthor): Result<Unit>? {
        return null
    }

    override suspend fun unfollow(locator: PlatformLocator, target: BlogAuthor): Result<Unit>? {
        return null
    }

    override suspend fun isFollowing(
        locator: PlatformLocator,
        target: BlogAuthor
    ): Result<Boolean>? {
        return null
    }

    override suspend fun translate(
        locator: PlatformLocator,
        status: Status,
        lan: String,
    ): Result<BlogTranslation>? {
        return null
    }
}
