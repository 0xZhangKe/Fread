package com.zhangke.fread.status.status

import com.zhangke.framework.collections.mapFirst
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import com.zhangke.fread.status.status.model.StatusInteraction
import com.zhangke.fread.status.uri.FormalUri

class StatusResolver(
    private val resolverList: List<IStatusResolver>,
) {

    suspend fun getStatus(
        role: IdentityRole,
        statusId: String,
        platform: BlogPlatform,
    ): Result<StatusUiState> {
        return resolverList.mapFirst { it.getStatus(role, statusId, platform) }
    }

    suspend fun getStatusList(
        role: IdentityRole,
        uri: FormalUri,
        limit: Int,
        minId: String? = null,
        maxId: String? = null,
    ): Result<List<StatusUiState>> {
        for (statusResolver in resolverList) {
            val result = statusResolver.getStatusList(
                role = role,
                uri = uri,
                limit = limit,
                minId = minId,
                maxId = maxId,
            )
            if (result != null) return result
        }
        return Result.failure(IllegalArgumentException("Unsupported uri:$uri!"))
    }

    suspend fun interactive(
        role: IdentityRole,
        status: Status,
        type: StatusActionType,
    ): Result<Status?> {
        return resolverList.mapFirst { it.interactive(role, status, type) }
    }

    suspend fun follow(
        role: IdentityRole,
        target: BlogAuthor,
    ): Result<Unit> {
        return resolverList.mapFirst { it.follow(role, target) }
    }

    suspend fun unfollow(
        role: IdentityRole,
        target: BlogAuthor,
    ): Result<Unit> {
        return resolverList.mapFirst { it.unfollow(role, target) }
    }

    suspend fun votePoll(
        role: IdentityRole,
        blog: Blog,
        votedOption: List<BlogPoll.Option>,
    ): Result<Status> {
        return resolverList.mapFirst { it.votePoll(role, blog, votedOption) }
    }

    suspend fun getStatusContext(role: IdentityRole, status: Status): Result<StatusContext> {
        return resolverList.mapFirst { it.getStatusContext(role, status) }
    }

    suspend fun getSuggestionAccounts(role: IdentityRole): Result<List<BlogAuthor>> {
        return resolverList.mapFirst {
            it.getSuggestionAccounts(role)
        }
    }

    suspend fun getHashtag(role: IdentityRole, limit: Int, offset: Int): Result<List<Hashtag>> {
        return resolverList.mapFirst { it.getHashtag(role, limit, offset) }
    }

    suspend fun getPublicTimeline(
        role: IdentityRole,
        limit: Int,
        maxId: String?,
    ): Result<List<StatusUiState>> {
        return resolverList.firstNotNullOf {
            it.getPublicTimeline(role, limit, maxId)
        }
    }

    suspend fun isFollowing(
        role: IdentityRole,
        target: BlogAuthor,
    ): Result<Boolean>? {
        return resolverList.firstNotNullOfOrNull { it.isFollowing(role, target) }
    }

    suspend fun translate(
        role: IdentityRole,
        status: Status,
        lan: String,
    ): Result<BlogTranslation>{
        return resolverList.firstNotNullOf { it.translate(role, status, lan) }
    }
}

interface IStatusResolver {

    suspend fun getStatus(
        role: IdentityRole,
        statusId: String,
        platform: BlogPlatform,
    ): Result<StatusUiState>?

    /**
     * @return null if un-support
     */
    suspend fun getStatusList(
        role: IdentityRole,
        uri: FormalUri,
        limit: Int,
        minId: String?,
        maxId: String?
    ): Result<List<StatusUiState>>?

    suspend fun interactive(
        role: IdentityRole,
        status: Status,
        type: StatusActionType,
    ): Result<Status?>?

    suspend fun votePoll(
        role: IdentityRole,
        blog: Blog,
        votedOption: List<BlogPoll.Option>,
    ): Result<Status>?

    suspend fun getStatusContext(role: IdentityRole, status: Status): Result<StatusContext>?

    suspend fun getSuggestionAccounts(role: IdentityRole): Result<List<BlogAuthor>>?

    suspend fun getHashtag(role: IdentityRole, limit: Int, offset: Int): Result<List<Hashtag>>?

    suspend fun getPublicTimeline(
        role: IdentityRole,
        limit: Int,
        maxId: String?,
    ): Result<List<StatusUiState>>?

    suspend fun follow(
        role: IdentityRole,
        target: BlogAuthor,
    ): Result<Unit>?

    suspend fun unfollow(
        role: IdentityRole,
        target: BlogAuthor,
    ): Result<Unit>?

    suspend fun isFollowing(
        role: IdentityRole,
        target: BlogAuthor,
    ): Result<Boolean>?

    suspend fun translate(
        role: IdentityRole,
        status: Status,
        lan: String,
    ): Result<BlogTranslation>?
}
