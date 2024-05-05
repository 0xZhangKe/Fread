package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.AllInOneRoleResolver
import com.zhangke.utopia.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.utopia.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import dagger.assisted.AssistedInject

class UserTimelineViewModel @AssistedInject constructor(
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val statusProvider: StatusProvider,
    buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    val role: IdentityRole,
    val webFinger: WebFinger,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
) {

    init {
        initController(
            coroutineScope = viewModelScope,
            roleResolver = AllInOneRoleResolver(role),
            loadFirstPageLocalFeeds = { Result.success(emptyList()) },
            loadNewFromServerFunction = ::loadNewFromServer,
            loadMoreFunction = ::loadMore,
            onStatusUpdate = {},
        )
        initFeeds(false)
    }

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        return loadUserTimeline().map {
            RefreshResult(
                newStatus = it,
                deletedStatus = emptyList(),
            )
        }
    }

    private suspend fun loadMore(maxId: String?): Result<List<Status>> {
        return loadUserTimeline(maxId)
    }

    private suspend fun loadUserTimeline(maxId: String? = null): Result<List<Status>> {
        val accountIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(webFinger, role)
        if (accountIdResult.isFailure) {
            return Result.failure(accountIdResult.exceptionOrNull()!!)
        }
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return clientManager.getClient(role)
            .accountRepo
            .getStatuses(
                id = accountIdResult.getOrThrow(),
                maxId = maxId,
            ).map { it.map { item -> statusAdapter.toStatus(item, platform) } }
    }
}
