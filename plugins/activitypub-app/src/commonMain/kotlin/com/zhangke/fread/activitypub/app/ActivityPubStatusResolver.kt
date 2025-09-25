package com.zhangke.fread.activitypub.app

import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTranslationEntityAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.uri.UserUriTransformer
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetStatusContextUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.GetUserStatusUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.fread.activitypub.app.internal.usecase.status.VotePollUseCase
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.PagedData
import com.zhangke.fread.status.model.PlatformLocator
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
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val translationAdapter: ActivityPubTranslationEntityAdapter,
) : IStatusResolver {

    override suspend fun getStatus(
        locator: PlatformLocator,
        blogId: String?,
        blogUri: String?,
        platform: BlogPlatform
    ): Result<StatusUiState>? {
        if (platform.protocol.notActivityPub) return null
        val statusRepo = clientManager.getClient(locator).statusRepo
        val loggedAccount = loggedAccountProvider.getAccount(locator)
        if (blogId.isNullOrEmpty()) return Result.failure(IllegalArgumentException("blogId is null or empty!"))
        return statusRepo.getStatuses(blogId)
            .mapCatching { entity ->
                if (entity == null) throw IllegalArgumentException("Can't find status(${blogId})")
                activityPubStatusAdapter.toStatusUiState(
                    entity = entity,
                    platform = platform,
                    locator = locator,
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
        val locator = PlatformLocator(baseUrl = userInsights.baseUrl)
        return getUserStatus(
            locator = locator,
            userInsights = userInsights,
            limit = limit,
            maxId = maxId,
        ).map {
            PagedData(it, it.lastOrNull()?.status?.id)
        }
    }

    override suspend fun interactive(
        locator: PlatformLocator,
        status: Status,
        type: StatusActionType,
    ): Result<Status?>? {
        if (status.notThisPlatform()) return null
        return statusInteractive(locator, status, type)
    }

    override suspend fun votePoll(
        locator: PlatformLocator,
        blog: Blog,
        votedOption: List<BlogPoll.Option>
    ): Result<Status>? {
        if (blog.platform.protocol.notActivityPub) return null
        return votePollUseCase(locator, blog, votedOption)
    }

    override suspend fun getStatusContext(
        locator: PlatformLocator,
        status: Status
    ): Result<StatusContext>? {
        if (status.notThisPlatform()) return null
        return getStatusContextUseCase(locator, status)
    }

    private fun Status.notThisPlatform(): Boolean {
        return this.platform.protocol.notActivityPub
    }

    override suspend fun follow(locator: PlatformLocator, target: BlogAuthor): Result<Unit>? {
        return updateRelationship(
            locator = locator,
            target = target,
            updater = {
                this.follow(it)
            }
        )
    }

    override suspend fun unfollow(locator: PlatformLocator, target: BlogAuthor): Result<Unit>? {
        return updateRelationship(
            locator = locator,
            target = target,
            updater = {
                this.unfollow(it)
            }
        )
    }

    private suspend fun updateRelationship(
        locator: PlatformLocator,
        target: BlogAuthor,
        updater: suspend AccountsRepo.(userId: String) -> Result<*>,
    ): Result<Unit>? {
        userUriTransformer.parse(target.uri) ?: return null
        val userId = if (target.userId.isNullOrEmpty()) {
            val userIdResult = webFingerBaseUrlToUserIdRepo.getUserId(target.webFinger, locator)
            if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
            userIdResult.getOrThrow()
        } else {
            target.userId!!
        }
        return clientManager.getClient(locator)
            .accountRepo
            .updater(userId)
            .map {}
    }

    override suspend fun isFollowing(
        locator: PlatformLocator,
        target: BlogAuthor
    ): Result<Boolean>? {
        userUriTransformer.parse(target.uri) ?: return null
        val userId = if (target.userId.isNullOrEmpty()) {
            val userIdResult = webFingerBaseUrlToUserIdRepo.getUserId(target.webFinger, locator)
            if (userIdResult.isFailure) return Result.failure(userIdResult.exceptionOrNull()!!)
            userIdResult.getOrThrow()
        } else {
            target.userId!!
        }
        return clientManager.getClient(locator)
            .accountRepo
            .getRelationships(listOf(userId))
            .map { it.firstOrNull()?.following ?: false }
    }

    override suspend fun translate(
        locator: PlatformLocator,
        status: Status,
        lan: String,
    ): Result<BlogTranslation>? {
        if (status.notThisPlatform()) return null
        return clientManager.getClient(locator)
            .statusRepo
            .translate(status.intrinsicBlog.id, lan)
            .map {
                translationAdapter.toTranslation(it)
            }
    }
}
