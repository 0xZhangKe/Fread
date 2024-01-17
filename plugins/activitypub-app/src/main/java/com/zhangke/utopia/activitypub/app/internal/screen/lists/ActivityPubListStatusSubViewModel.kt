package com.zhangke.utopia.activitypub.app.internal.screen.lists

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivityPubListStatusSubViewModel(
    private val platformRepo: ActivityPubPlatformRepo,
    private val listStatusRepo: ListStatusRepo,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val serverBaseUrl: FormalBaseUrl,
    private val listId: String,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(
        ActivityPubListStatusUiState(
            status = emptyList(),
            refreshing = false,
            loadMoreState = LoadState.Idle,
            errorMessage = null,
        )
    )
    val uiState: StateFlow<ActivityPubListStatusUiState> = _uiState.asStateFlow()

    private val _snackMessage = MutableSharedFlow<TextString>()
    val snackMessage: SharedFlow<TextString> = _snackMessage.asSharedFlow()

    private var blogPlatform: BlogPlatform? = null

    init {
        launchInViewModel {
            val statusFromLocal = listStatusRepo.getLocalStatus(
                serverBaseUrl = serverBaseUrl,
                listId = listId,
            )
            val platformResult = getBlogPlatform()
            if (platformResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = textOf(platformResult.exceptionOrNull()!!.message.orEmpty()),
                )
                return@launchInViewModel
            }
            val platform = platformResult.getOrThrow()
            _uiState.value = _uiState.value.copy(
                status = statusFromLocal.toUiStates(platform),
            )
            refreshStatus(false)
        }
    }

    fun onRefresh() {
        launchInViewModel {
            refreshStatus(true)
        }
    }

    private suspend fun refreshStatus(showRefreshing: Boolean) {
        _uiState.value = _uiState.value.copy(
            refreshing = showRefreshing,
        )
        val platformResult = getBlogPlatform()
        if (platformResult.isFailure) {
            _uiState.value = _uiState.value.copy(
                refreshing = false,
            )
            _snackMessage.emit(textOf(platformResult.exceptionOrNull()!!.message.orEmpty()))
            return
        }
        val platform = platformResult.getOrThrow()
        listStatusRepo.getRemoteStatus(
            serverBaseUrl = serverBaseUrl,
            listId = listId,
        ).onSuccess {
            _uiState.value = _uiState.value.copy(
                status = it.toUiStates(platform),
                refreshing = false,
            )
        }.onFailure {
            _uiState.value = _uiState.value.copy(
                refreshing = false,
            )
            _snackMessage.emit(textOf(it.message.orEmpty()))
        }
    }

    fun onLoadMore() {
        if (_uiState.value.refreshing || _uiState.value.loadMoreState == LoadState.Loading) return
        val latestStatus = _uiState.value.status.lastOrNull() ?: return
        launchInViewModel {
            _uiState.value = _uiState.value.copy(
                loadMoreState = LoadState.Loading,
            )
            val platformResult = getBlogPlatform()
            if (platformResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    loadMoreState = LoadState.Failed(platformResult.exceptionOrNull()!!),
                )
                return@launchInViewModel
            }
            val platform = platformResult.getOrThrow()
            listStatusRepo.loadMore(
                serverBaseUrl = serverBaseUrl,
                listId = listId,
                maxId = latestStatus.status.id,
            ).onSuccess {
                val currentState = _uiState.value
                _uiState.value = currentState.copy(
                    status = currentState.status + it.toUiStates(platform),
                    loadMoreState = LoadState.Idle,
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(
                    loadMoreState = LoadState.Failed(it),
                )
            }
        }
    }

    fun onInteractive(status: Status, interaction: StatusUiInteraction) {
    }

    private suspend fun List<ActivityPubStatusEntity>.toUiStates(platform: BlogPlatform): List<StatusUiState> {
        return this.map { entity ->
            val supportActions = getStatusSupportAction(entity)
            val status = statusAdapter.toStatus(entity, platform, supportActions)
            buildStatusUiState(status)
        }
    }

    private suspend fun getBlogPlatform(): Result<BlogPlatform> {
        if (blogPlatform != null) {
            return Result.success(blogPlatform!!)
        }
        return platformRepo.getPlatform(serverBaseUrl)
            .onSuccess { blogPlatform = it }
    }
}
