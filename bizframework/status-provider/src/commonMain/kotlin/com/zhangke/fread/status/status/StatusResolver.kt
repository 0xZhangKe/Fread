package com.zhangke.fread.status.status

import com.zhangke.framework.collections.mapFirst
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.PagedData
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import com.zhangke.fread.status.uri.FormalUri

class StatusResolver(
    private val resolverList: List<IStatusResolver>,
) {

    suspend fun getStatus(
        locator: PlatformLocator,
        blog: Blog,
        platform: BlogPlatform,
    ): Result<StatusUiState> {
        return resolverList.mapFirst { it.getStatus(locator, blog, platform) }
    }

    suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        maxId: String? = null,
    ): Result<PagedData<StatusUiState>> {
        for (statusResolver in resolverList) {
            val result = statusResolver.getStatusList(
                uri = uri,
                limit = limit,
                maxId = maxId,
            )
            if (result != null) return result
        }
        return Result.failure(IllegalArgumentException("Unsupported uri:$uri!"))
    }

    suspend fun interactive(
        locator: PlatformLocator,
        status: Status,
        type: StatusActionType,
    ): Result<Status?> {
        return resolverList.mapFirst { it.interactive(locator, status, type) }
    }

    suspend fun follow(
        locator: PlatformLocator,
        target: BlogAuthor,
    ): Result<Unit> {
        return resolverList.mapFirst { it.follow(locator, target) }
    }

    suspend fun unfollow(
        locator: PlatformLocator,
        target: BlogAuthor,
    ): Result<Unit> {
        return resolverList.mapFirst { it.unfollow(locator, target) }
    }

    suspend fun votePoll(
        locator: PlatformLocator,
        blog: Blog,
        votedOption: List<BlogPoll.Option>,
    ): Result<Status> {
        return resolverList.mapFirst { it.votePoll(locator, blog, votedOption) }
    }

    suspend fun getStatusContext(locator: PlatformLocator, status: Status): Result<StatusContext> {
        return resolverList.mapFirst { it.getStatusContext(locator, status) }
    }

    suspend fun isFollowing(
        locator: PlatformLocator,
        target: BlogAuthor,
    ): Result<Boolean>? {
        return resolverList.firstNotNullOfOrNull { it.isFollowing(locator, target) }
    }

    suspend fun translate(
        locator: PlatformLocator,
        status: Status,
        lan: String,
    ): Result<BlogTranslation> {
        return resolverList.firstNotNullOf { it.translate(locator, status, lan) }
    }
}

interface IStatusResolver {

    suspend fun getStatus(
        locator: PlatformLocator,
        blog: Blog,
        platform: BlogPlatform,
    ): Result<StatusUiState>?

    /**
     * @return null if un-support
     */
    suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        maxId: String?
    ): Result<PagedData<StatusUiState>>?

    suspend fun interactive(
        locator: PlatformLocator,
        status: Status,
        type: StatusActionType,
    ): Result<Status?>?

    suspend fun votePoll(
        locator: PlatformLocator,
        blog: Blog,
        votedOption: List<BlogPoll.Option>,
    ): Result<Status>?

    suspend fun getStatusContext(locator: PlatformLocator, status: Status): Result<StatusContext>?

    suspend fun follow(
        locator: PlatformLocator,
        target: BlogAuthor,
    ): Result<Unit>?

    suspend fun unfollow(
        locator: PlatformLocator,
        target: BlogAuthor,
    ): Result<Unit>?

    suspend fun isFollowing(
        locator: PlatformLocator,
        target: BlogAuthor,
    ): Result<Boolean>?

    suspend fun translate(
        locator: PlatformLocator,
        status: Status,
        lan: String,
    ): Result<BlogTranslation>?
}
