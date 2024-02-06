package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.status.model.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = UserTimelineViewModel.Factory::class)
class UserTimelineViewModel @AssistedInject constructor(
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    platformRepo: ActivityPubPlatformRepo,
    statusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    private val baseUrlManager: BaseUrlManager,
    interactiveHandler: ActivityPubInteractiveHandler,
    @Assisted val userUriInsights: UserUriInsights,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(userUriInsights: UserUriInsights): UserTimelineViewModel
    }

    private val loadableController = ActivityPubStatusLoadController(
        statusAdapter = statusAdapter,
        platformRepo = platformRepo,
        coroutineScope = viewModelScope,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
    )

    val uiState = loadableController.uiState
    val errorMessageFlow = loadableController.errorMessageFlow

    init {
        launchInViewModel {
            loadableController.initStatusData(
                baseUrl = getBaseUrl(),
                getStatusFromServer = { loadStatus(it) },
            )
        }
    }

    fun refresh() {
        launchInViewModel {
            val baseUrl = getBaseUrl()
            loadableController.onRefresh(
                baseUrl = baseUrl,
                getStatusFromServer = { loadStatus(baseUrl) },
            )
        }
    }

    fun loadMore() {
        launchInViewModel {
            loadableController.onLoadMore(
                baseUrl = getBaseUrl(),
                loadMoreFunction = { maxId, baseUrl -> loadStatus(baseUrl, maxId) },
            )
        }
    }

    private suspend fun loadStatus(
        baseUrl: FormalBaseUrl,
        maxId: String? = null,
    ): Result<List<ActivityPubStatusEntity>> {
        val accountIdResult = webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, baseUrl)
        if (accountIdResult.isFailure) {
            return Result.failure(accountIdResult.exceptionOrNull()!!)
        }
        return getClient().accountRepo.getStatuses(
            id = accountIdResult.getOrThrow(),
            maxId = maxId,
        )
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        loadableController.onInteractive(status, uiInteraction)
    }

    private suspend fun getClient(): ActivityPubClient {
        return clientManager.getClient(getBaseUrl())
    }

    private suspend fun getBaseUrl(): FormalBaseUrl {
        return baseUrlManager.decideBaseUrl(userUriInsights.baseUrl)
    }
}
