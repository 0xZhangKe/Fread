package com.zhangke.utopia.activitypub.app

import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusContextUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetUserStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.VotePollUseCase
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.status.IStatusResolver
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.status.model.StatusContext
import com.zhangke.utopia.status.status.model.StatusInteraction
import com.zhangke.utopia.status.uri.FormalUri
import javax.inject.Inject

class ActivityPubStatusResolver @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val getUserStatus: GetUserStatusUseCase,
    private val userUriTransformer: UserUriTransformer,
    private val statusInteractive: StatusInteractiveUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val getStatusContextUseCase: GetStatusContextUseCase,
    private val votePoll: VotePollUseCase,
    private val hashtagAdapter: ActivityPubTagAdapter,
    private val accountAdapter: ActivityPubAccountEntityAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
) : IStatusResolver {

    override suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        sinceId: String?,
        maxId: String?,
    ): Result<List<Status>>? {
        val userInsights = userUriTransformer.parse(uri)
        if (userInsights != null) {
            return getUserStatus(
                userInsights = userInsights,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
        }
        return null
    }

    override suspend fun interactive(
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status>? {
        if (status.notThisPlatform()) return null
        return statusInteractive(status, interaction).map { entity ->
            val platform = status.platform
            activityPubStatusAdapter.toStatus(entity, platform)
        }
    }

    override suspend fun votePoll(
        status: Status,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        if (status.notThisPlatform()) return null
        return votePoll(status, votedOption, status.platform.baseUrl)
    }

    override suspend fun getStatusContext(status: Status): Result<StatusContext>? {
        if (status.notThisPlatform()) return null
        return getStatusContextUseCase(status)
    }

    private fun Status.notThisPlatform(): Boolean {
        return this.platform.protocol.id != ACTIVITY_PUB_PROTOCOL_ID
    }

    override suspend fun getSuggestionAccounts(uri: FormalUri): Result<List<BlogAuthor>>? {
        val uriInsights = userUriTransformer.parse(uri) ?: return null
        return clientManager.getClient(uriInsights.baseUrl)
            .accountRepo
            .getSuggestions()
            .map { list -> list.map { accountAdapter.toAuthor(it.account) } }
    }

    override suspend fun getHashtag(
        userUri: FormalUri,
        limit: Int,
        offset: Int
    ): Result<List<Hashtag>>? {
        val uriInsights = userUriTransformer.parse(userUri) ?: return null
        return clientManager.getClient(uriInsights.baseUrl)
            .instanceRepo
            .getTrendsTags(limit = limit, offset = offset)
            .map { list -> list.map { hashtagAdapter.adapt(it) } }
    }

    override suspend fun getPublicTimeline(
        userUri: FormalUri,
        limit: Int,
        sinceId: String?
    ): Result<List<Status>>? {
        val uriInsights = userUriTransformer.parse(userUri) ?: return null
        val baseUrl = uriInsights.baseUrl
        val platformResult = platformRepo.getPlatform(baseUrl)
        if (platformResult.isFailure) return Result.failure(platformResult.exceptionOrNull()!!)
        val platform = platformResult.getOrThrow()
        return clientManager.getClient(baseUrl)
            .timelinesRepo
            .publicTimelines(limit = limit, sinceId = sinceId)
            .map { list -> list.map { activityPubStatusAdapter.toStatus(it, platform) } }
    }
}
