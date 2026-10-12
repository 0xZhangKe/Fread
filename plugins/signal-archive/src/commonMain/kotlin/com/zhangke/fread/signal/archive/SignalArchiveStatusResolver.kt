package com.zhangke.fread.signal.archive

import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.PagedData
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.IStatusResolver
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import com.zhangke.fread.status.uri.FormalUri

class SignalArchiveStatusResolver : IStatusResolver {

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
        return null
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
        return null
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
