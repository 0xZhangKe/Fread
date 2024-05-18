package com.zhangke.utopia.activitypub.app

import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusContextUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetUserStatusUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.VotePollUseCase
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.IdentityRole
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
    private val votePollUseCase: VotePollUseCase,
    private val hashtagAdapter: ActivityPubTagAdapter,
    private val accountAdapter: ActivityPubAccountEntityAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
) : IStatusResolver {

    override suspend fun getStatusList(
        role: IdentityRole,
        uri: FormalUri,
        limit: Int,
        sinceId: String?,
        maxId: String?,
    ): Result<List<Status>>? {
        val userInsights = userUriTransformer.parse(uri)
        if (userInsights != null) {
            return getUserStatus(
                role = role,
                userInsights = userInsights,
                limit = limit,
                sinceId = sinceId,
                maxId = maxId,
            )
        }
        return null
    }

    override suspend fun interactive(
        role: IdentityRole,
        status: Status,
        interaction: StatusInteraction,
    ): Result<Status>? {
        if (status.notThisPlatform()) return null
        return statusInteractive(role, status, interaction).map { entity ->
            val platform = status.platform
            if (interaction is StatusInteraction.Forward && entity.reblog != null) {
                activityPubStatusAdapter.toStatus(entity.reblog!!, platform)
            } else {
                activityPubStatusAdapter.toStatus(entity, platform)
            }
        }
    }

    override suspend fun votePoll(
        role: IdentityRole,
        status: Status,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        if (status.notThisPlatform()) return null
        return votePollUseCase(role, status, votedOption)
    }

    override suspend fun getStatusContext(
        role: IdentityRole,
        status: Status
    ): Result<StatusContext>? {
        if (status.notThisPlatform()) return null
        return getStatusContextUseCase(role, status)
    }

    private fun Status.notThisPlatform(): Boolean {
        return this.platform.protocol.id != ACTIVITY_PUB_PROTOCOL_ID
    }

    override suspend fun getSuggestionAccounts(role: IdentityRole): Result<List<BlogAuthor>>? {
        return clientManager.getClient(role)
            .accountRepo
            .getSuggestions()
            .map { list -> list.map { accountAdapter.toAuthor(it.account) } }
    }

    override suspend fun getHashtag(
        role: IdentityRole,
        limit: Int,
        offset: Int,
    ): Result<List<Hashtag>> {
        return clientManager.getClient(role)
            .instanceRepo
            .getTrendsTags(limit = limit, offset = offset)
            .map { list -> list.map { hashtagAdapter.adapt(it) } }
    }

    override suspend fun getPublicTimeline(
        role: IdentityRole,
        limit: Int,
        sinceId: String?,
    ): Result<List<Status>> {
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) return Result.failure(platformResult.exceptionOrNull()!!)
        val platform = platformResult.getOrThrow()
        return clientManager.getClient(role)
            .timelinesRepo
            .publicTimelines(limit = limit, sinceId = sinceId)
            .map { list -> list.map { activityPubStatusAdapter.toStatus(it, platform) } }
    }

    override suspend fun follow(role: IdentityRole, target: BlogAuthor): Result<Unit>? {
        return updateRelationship(
            role = role,
            target = target,
            updater = {
                this.follow(it)
            }
        )
    }

    override suspend fun unfollow(role: IdentityRole, target: BlogAuthor): Result<Unit>? {
        return updateRelationship(
            role = role,
            target = target,
            updater = {
                this.unfollow(it)
            }
        )
    }

    private suspend fun updateRelationship(
        role: IdentityRole,
        target: BlogAuthor,
        updater: suspend AccountsRepo.(userId: String) -> Result<*>,
    ): Result<Unit>? {
        userUriTransformer.parse(target.uri) ?: return null
        val userIdResult = webFingerBaseUrlToUserIdRepo.getUserId(target.webFinger, role)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        return clientManager.getClient(role)
            .accountRepo
            .updater(userId)
            .map {}
    }
}
