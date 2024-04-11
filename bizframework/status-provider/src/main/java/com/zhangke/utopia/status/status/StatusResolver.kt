package com.zhangke.utopia.status.status

import com.zhangke.framework.collections.mapFirst
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import com.zhangke.utopia.status.status.model.StatusInteraction
import com.zhangke.utopia.status.uri.FormalUri

class StatusResolver(
    private val resolverList: List<IStatusResolver>,
) {

    suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        sinceId: String? = null,
        maxId: String? = null,
    ): Result<List<Status>> {
        for (statusResolver in resolverList) {
            val result = statusResolver.getStatusList(
                uri = uri,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
            if (result != null) return result
        }
        return Result.failure(IllegalArgumentException("Unsupported uri:$uri!"))
    }

    suspend fun interactive(status: Status, interaction: StatusInteraction): Result<Status> {
        return resolverList.mapFirst { it.interactive(status, interaction) }
    }

    suspend fun follow(
        account: LoggedAccount,
        target: BlogAuthor,
    ): Result<Unit> {
        return resolverList.mapFirst { it.follow(account, target) }
    }

    suspend fun unfollow(
        account: LoggedAccount,
        target: BlogAuthor,
    ): Result<Unit> {
        return resolverList.mapFirst { it.unfollow(account, target) }
    }

    suspend fun votePoll(status: Status, votedOption: List<BlogPoll.Option>): Result<Status> {
        return resolverList.mapFirst { it.votePoll(status, votedOption) }
    }

    suspend fun getStatusContext(baseUrl: FormalBaseUrl, status: Status): Result<StatusContext> {
        return resolverList.mapFirst { it.getStatusContext(baseUrl, status) }
    }

    suspend fun getSuggestionAccounts(baseUrl: FormalBaseUrl): Result<List<BlogAuthor>> {
        return resolverList.mapFirst {
            it.getSuggestionAccounts(baseUrl)
        }
    }

    suspend fun getHashtag(baseUrl: FormalBaseUrl, limit: Int, offset: Int): Result<List<Hashtag>> {
        return resolverList.mapFirst { it.getHashtag(baseUrl, limit, offset) }
    }

    suspend fun getPublicTimeline(
        baseUrl: FormalBaseUrl,
        limit: Int,
        sinceId: String?,
    ): Result<List<Status>> {
        return resolverList.mapFirst {
            it.getPublicTimeline(baseUrl, limit, sinceId)
        }
    }
}

interface IStatusResolver {

    /**
     * @return null if un-support
     */
    suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        sinceId: String?,
        maxId: String?
    ): Result<List<Status>>?

    suspend fun interactive(status: Status, interaction: StatusInteraction): Result<Status>?

    suspend fun votePoll(status: Status, votedOption: List<BlogPoll.Option>): Result<Status>?

    suspend fun getStatusContext(baseUrl: FormalBaseUrl, status: Status): Result<StatusContext>?

    suspend fun getSuggestionAccounts(baseUrl: FormalBaseUrl): Result<List<BlogAuthor>>?

    suspend fun getHashtag(baseUrl: FormalBaseUrl, limit: Int, offset: Int): Result<List<Hashtag>>?

    suspend fun getPublicTimeline(
        baseUrl: FormalBaseUrl,
        limit: Int,
        sinceId: String?,
    ): Result<List<Status>>?

    suspend fun follow(
        account: LoggedAccount,
        target: BlogAuthor,
    ): Result<Unit>?

    suspend fun unfollow(
        account: LoggedAccount,
        target: BlogAuthor,
    ): Result<Unit>?
}
