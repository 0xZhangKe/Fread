package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.screen.content.FeedsStatusUiState
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubStatusLoadController
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.model.updateById
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = UserTimelineViewModel.Factory::class)
class UserTimelineViewModel @AssistedInject constructor(
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    private val baseUrlManager: BaseUrlManager,
    private val statusInteractive: StatusInteractiveUseCase,
    private val interactiveHandler: ActivityPubInteractiveHandler,
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


    private val _messageFlow = MutableSharedFlow<TextString>()
    val messageFlow = _messageFlow.asSharedFlow()

    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        refresh()
    }

    fun refresh() {
        if (_uiState.value.refreshing) {
            return
        }
        refreshJob?.cancel()
        _uiState.update { it.copy(refreshing = true) }
        refreshJob = launchInViewModel {
            getPlatformAndAccountId()
                .mapCatching { (platform, accountId) ->
                    getClient().accountRepo.getStatuses(id = accountId).getOrThrow()
                        .toUiState(platform)
                }
                .onFailure { e ->
                    e.message?.let { _messageFlow.emit(textOf(it)) }
                    _uiState.update { it.copy(refreshing = false) }
                }.onSuccess { status ->
                    _uiState.update {
                        it.copy(
                            status = status,
                            refreshing = false,
                        )
                    }
                }
        }
    }

    fun loadMore() {
        if (_uiState.value.loadMoreState == LoadState.Loading) {
            return
        }
        if (_uiState.value.status.isEmpty()) return
        loadMoreJob?.cancel()
        loadMoreJob = launchInViewModel {
            getPlatformAndAccountId()
                .mapCatching { (platform, accountId) ->
                    getClient().accountRepo
                        .getStatuses(
                            id = accountId,
                            maxId = _uiState.value.status.last().status.id,
                        )
                        .getOrThrow()
                        .toUiState(platform)
                }
                .onFailure { e ->
                    e.message?.let { _messageFlow.emit(textOf(it)) }
                    _uiState.update { it.copy(loadMoreState = LoadState.Failed(e)) }
                }.onSuccess { status ->
                    _uiState.update {
                        it.copy(
                            status = it.status.plus(status),
                            loadMoreState = LoadState.Idle,
                        )
                    }
                }
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        val interaction = uiInteraction.statusInteraction ?: return
        launchInViewModel {
            statusInteractive(status, interaction)
                .onSuccess { newStatus ->
                    _uiState.update { current ->
                        current.copy(
                            status = current.status.updateById(newStatus.id) {
                                newStatus.toUiState(status.platform)
                            }
                        )
                    }
                }.onFailure {
                    _messageFlow.emit(textOf(it.message.orEmpty()))
                }
        }
    }

    private suspend fun getPlatformAndAccountId(): Result<Pair<BlogPlatform, String>> {
        val baseUrl = getBaseUrl()
        return platformRepo.getPlatform(baseUrl)
            .mapCatching {
                val accountId = webFingerBaseUrlToUserIdRepo
                    .getUserId(userUriInsights.webFinger, baseUrl)
                    .getOrThrow()
                it to accountId
            }
    }

    private suspend fun getClient(): ActivityPubClient {
        return clientManager.getClient(getBaseUrl())
    }

    private suspend fun getBaseUrl(): FormalBaseUrl {
        return baseUrlManager.decideBaseUrl(userUriInsights.baseUrl)
    }

    private suspend fun List<ActivityPubStatusEntity>.toUiState(platform: BlogPlatform): List<StatusUiState> {
        return this.map { it.toUiState(platform) }
    }

    private suspend fun ActivityPubStatusEntity.toUiState(platform: BlogPlatform): StatusUiState {
        return buildStatusUiState(statusAdapter.toStatus(this, platform))
    }
}
