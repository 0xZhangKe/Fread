package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.activitypub.ActivityPubClient
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.baseurl.BaseUrlManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
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
    @Assisted val userUriInsights: UserUriInsights,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(userUriInsights: UserUriInsights): UserTimelineViewModel
    }

    private val _uiState = MutableStateFlow(
        UserTimelineUiState(
            status = emptyList(),
            refreshing = false,
            loading = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _messageFlow = MutableSharedFlow<TextString>()
    val messageFlow = _messageFlow.asSharedFlow()

    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        refresh()
    }

    private fun refresh() {
        if (_uiState.value.refreshing) {
            return
        }
        refreshJob?.cancel()
        _uiState.update { it.copy(refreshing = true) }
        refreshJob = launchInViewModel {
            getPlatformAndAccountId()
                .mapCatching { (platform, accountId) ->
                    getClient().accountRepo.getStatuses(id = accountId).getOrThrow().toUiState(platform)
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
        if (_uiState.value.loading) {
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
                    _uiState.update { it.copy(loading = false) }
                }.onSuccess { status ->
                    _uiState.update {
                        it.copy(
                            status = it.status.plus(status),
                            loading = false,
                        )
                    }
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
        return this.map { buildStatusUiState(statusAdapter.toStatus(it, platform)) }
    }
}
