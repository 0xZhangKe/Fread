package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
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
    interactiveHandler: ActivityPubInteractiveHandler,
    pollAdapter: ActivityPubPollAdapter,
    @Assisted val role: IdentityRole,
    @Assisted val userUriInsights: UserUriInsights,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(role: IdentityRole, userUriInsights: UserUriInsights): UserTimelineViewModel
    }

    private val loadableController = ActivityPubStatusLoadController(
        statusAdapter = statusAdapter,
        clientManager = clientManager,
        platformRepo = platformRepo,
        coroutineScope = viewModelScope,
        pollAdapter = pollAdapter,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
    )

    val uiState = loadableController.uiState
    val errorMessageFlow = loadableController.errorMessageFlow

    init {
        launchInViewModel {
            loadableController.initStatusData(
                role = role,
                getStatusFromServer = { loadStatus(it) },
            )
        }
    }

    fun refresh() {
        launchInViewModel {
            loadableController.onRefresh(
                role = role,
                getStatusFromServer = { loadStatus(it) },
            )
        }
    }

    fun loadMore() {
        launchInViewModel {
            loadableController.onLoadMore(
                role = role,
                loadMoreFunction = { maxId, role -> loadStatus(role, maxId) },
            )
        }
    }

    private suspend fun loadStatus(
        role: IdentityRole,
        maxId: String? = null,
    ): Result<List<ActivityPubStatusEntity>> {
        val accountIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(userUriInsights.webFinger, role)
        if (accountIdResult.isFailure) {
            return Result.failure(accountIdResult.exceptionOrNull()!!)
        }
        return getClient().accountRepo.getStatuses(
            id = accountIdResult.getOrThrow(),
            maxId = maxId,
        )
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        loadableController.onInteractive(role, status, uiInteraction)
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        loadableController.onVoted(role, status, options)
    }

    private fun getClient(): ActivityPubClient {
        return clientManager.getClient(role)
    }
}
