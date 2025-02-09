package com.zhangke.fread.activitypub.app

import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTranslationEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetStatusContextUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetUserStatusUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.VotePollUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.PagedData
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.notActivityPub
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.status.IStatusResolver
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.status.model.StatusContext
import com.zhangke.fread.status.uri.FormalUri
import me.tatarka.inject.annotations.Inject

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
    private val loggedAccountProvider: LoggedAccountProvider,
    private val translationAdapter: ActivityPubTranslationEntityAdapter,
) : IStatusResolver {

    override suspend fun getStatus(
        role: IdentityRole,
        statusId: String,
        platform: BlogPlatform
    ): Result<StatusUiState>? {
        if (platform.protocol.notActivityPub) return null
        val statusRepo = clientManager.getClient(role).statusRepo
        val loggedAccount = loggedAccountProvider.getAccount(role)
        return statusRepo.getStatuses(statusId)
            .mapCatching { entity ->
                if (entity == null) throw RuntimeException("Can't find status(${statusId})")
                activityPubStatusAdapter.toStatusUiState(
                    entity = entity,
                    platform = platform,
                    role = role,
                    loggedAccount = loggedAccount,
                )
            }
    }

    override suspend fun getStatusList(
        uri: FormalUri,
        limit: Int,
        maxId: String?,
    ): Result<PagedData<StatusUiState>>? {
        val userInsights = userUriTransformer.parse(uri) ?: return null
        val role = IdentityRole(baseUrl = userInsights.baseUrl)
        return getUserStatus(
            role = role,
            userInsights = userInsights,
            limit = limit,
            maxId = maxId,
        ).map {
            PagedData(it, it.lastOrNull()?.status?.id)
        }
    }

    override suspend fun interactive(
        role: IdentityRole,
        status: Status,
        type: StatusActionType,
    ): Result<Status?>? {
        if (status.notThisPlatform()) return null
        return statusInteractive(role, status, type)
    }

    override suspend fun votePoll(
        role: IdentityRole,
        blog: Blog,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        if (blog.platform.protocol.notActivityPub) return null
        return votePollUseCase(role, blog, votedOption)
    }

    override suspend fun getStatusContext(
        role: IdentityRole,
        status: Status
    ): Result<StatusContext>? {
        if (status.notThisPlatform()) return null
        return getStatusContextUseCase(role, status)
    }

    private fun Status.notThisPlatform(): Boolean {
        return this.platform.protocol.notActivityPub
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
        maxId: String?,
    ): Result<List<StatusUiState>> {
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) return Result.failure(platformResult.exceptionOrNull()!!)
        val platform = platformResult.getOrThrow()
        val loggedAccount = loggedAccountProvider.getAccount(role)
        return clientManager.getClient(role)
            .timelinesRepo
            .publicTimelines(limit = limit, maxId = maxId)
            .map { list ->
                list.map {
                    activityPubStatusAdapter.toStatusUiState(
                        entity = it,
                        platform = platform,
                        role = role,
                        loggedAccount = loggedAccount,
                    )
                }
            }
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

    override suspend fun isFollowing(role: IdentityRole, target: BlogAuthor): Result<Boolean>? {
        userUriTransformer.parse(target.uri) ?: return null
        val userIdResult = webFingerBaseUrlToUserIdRepo.getUserId(target.webFinger, role)
        if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
        val userId = userIdResult.getOrThrow()
        return clientManager.getClient(role)
            .accountRepo
            .getRelationships(listOf(userId))
            .map { it.firstOrNull()?.following ?: false }
    }

    override suspend fun translate(
        role: IdentityRole,
        status: Status,
        lan: String,
    ): Result<BlogTranslation>? {
        if (status.notThisPlatform()) return null
        return clientManager.getClient(role)
            .statusRepo
            .translate(status.intrinsicBlog.id, lan)
            .map {
                translationAdapter.toTranslation(it)
            }
    }
}
