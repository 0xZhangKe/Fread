package com.zhangke.utopia.status.status

import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import com.zhangke.utopia.status.status.model.StatusInteraction
import com.zhangke.utopia.status.uri.FormalUri

class StatusResolver(
    private val resolverList: List<IStatusResolver>,
) {

    suspend fun getStatusList(
        role: IdentityRole,
        uri: FormalUri,
        limit: Int,
        sinceId: String? = null,
        maxId: String? = null,
    ): Result<List<Status>> {
        for (statusResolver in resolverList) {
            val result = statusResolver.getStatusList(
                role = role,
                uri = uri,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
            if (result != null) return result
        }
        return Result.failure(IllegalArgumentException("Unsupported uri:$uri!"))
    }

    suspend fun interactive(
        role: IdentityRole,
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status> {
        return resolverList.mapFirst { it.interactive(role, status, interaction) }
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
        status: Status,
        votedOption: List<BlogPoll.Option>,
    ): Result<Status> {
        return resolverList.mapFirst { it.votePoll(role, status, votedOption) }
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
        sinceId: String?,
    ): Result<List<Status>> {
        return resolverList.mapFirst {
            it.getPublicTimeline(role, limit, sinceId)
        }
    }
}

interface IStatusResolver {

    /**
     * @return null if un-support
     */
    suspend fun getStatusList(
        role: IdentityRole,
        uri: FormalUri,
        limit: Int,
        sinceId: String?,
        maxId: String?
    ): Result<List<Status>>?

    suspend fun interactive(
        role: IdentityRole,
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status>?

    suspend fun votePoll(
        role: IdentityRole,
        status: Status,
        votedOption: List<BlogPoll.Option>,
    ): Result<Status>?

    suspend fun getStatusContext(role: IdentityRole, status: Status): Result<StatusContext>?

    suspend fun getSuggestionAccounts(role: IdentityRole): Result<List<BlogAuthor>>?

    suspend fun getHashtag(role: IdentityRole, limit: Int, offset: Int): Result<List<Hashtag>>?

    suspend fun getPublicTimeline(
        role: IdentityRole,
        limit: Int,
        sinceId: String?,
    ): Result<List<Status>>?

    suspend fun follow(
        role: IdentityRole,
        target: BlogAuthor,
    ): Result<Unit>?

    suspend fun unfollow(
        role: IdentityRole,
        target: BlogAuthor,
    ): Result<Unit>?
}
