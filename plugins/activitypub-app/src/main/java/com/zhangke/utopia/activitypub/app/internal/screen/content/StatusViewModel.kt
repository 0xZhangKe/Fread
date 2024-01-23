package com.zhangke.utopia.activitypub.app.internal.screen.content

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.model.updateById
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class StatusViewModel(
    private val platformRepo: ActivityPubPlatformRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val statusInteractive: StatusInteractiveUseCase,
    protected val serverBaseUrl: FormalBaseUrl,
) : SubViewModel() {

    abstract suspend fun getLocalStatus(): List<ActivityPubStatusEntity>

    abstract suspend fun getRemoteStatus(): Result<List<ActivityPubStatusEntity>>

    abstract suspend fun loadMore(maxId: String): Result<List<ActivityPubStatusEntity>>

    abstract suspend fun updateLocalStatus(status: ActivityPubStatusEntity)

    protected val _uiState = MutableStateFlow(
        FeedsStatusUiState(
            status = emptyList(),
            refreshing = false,
            loadMoreState = LoadState.Idle,
            errorMessage = null,
        )
    )
    val uiState: StateFlow<FeedsStatusUiState> = _uiState.asStateFlow()

    private val _snackMessage = MutableSharedFlow<TextString>()
    val snackMessage: SharedFlow<TextString> = _snackMessage.asSharedFlow()

    private var blogPlatform: BlogPlatform? = null

    protected fun prepare() {
        launchInViewModel {
            val statusFromLocal = getLocalStatus()
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
            refreshStatus(statusFromLocal.isEmpty())
        }
    }

    fun onRefresh() {
        launchInViewModel {
            refreshStatus(true)
        }
    }

    private suspend fun refreshStatus(showRefreshing: Boolean) {
        updateRefreshState(showRefreshing, true, null)
        val platformResult = getBlogPlatform()
        if (platformResult.isFailure) {
            val errorMessage = textOf(platformResult.exceptionOrNull()!!.message.orEmpty())
            updateRefreshState(showRefreshing, false, errorMessage)
            _snackMessage.emit(errorMessage)
            return
        }
        val platform = platformResult.getOrThrow()
        getRemoteStatus().onSuccess {
            _uiState.value = _uiState.value.copy(
                status = it.toUiStates(platform),
                refreshing = false,
                errorMessage = null,
            )
        }.onFailure {
            val errorMessage = textOf(it.message.orEmpty())
            updateRefreshState(showRefreshing, false, errorMessage)
            _snackMessage.emit(errorMessage)
        }
    }

    private fun updateRefreshState(
        showRefreshing: Boolean,
        refreshing: Boolean,
        errorMessage: TextString? = _uiState.value.errorMessage,
    ) {
        _uiState.value = _uiState.value.copy(
            refreshing = showRefreshing && refreshing,
            errorMessage = errorMessage,
        )
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
            loadMore(maxId = latestStatus.status.id)
                .onSuccess {
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
                    updateLocalStatus(newStatus)
                }.onFailure {
                    _snackMessage.emit(textOf(it.message.orEmpty()))
                }
        }
    }

    private suspend fun List<ActivityPubStatusEntity>.toUiStates(platform: BlogPlatform): List<StatusUiState> {
        return this.map { entity ->
            entity.toUiState(platform)
        }
    }

    private suspend fun ActivityPubStatusEntity.toUiState(platform: BlogPlatform): StatusUiState {
        val status = statusAdapter.toStatus(this, platform)
        return buildStatusUiState(status)
    }

    private suspend fun getBlogPlatform(): Result<BlogPlatform> {
        if (blogPlatform != null) {
            return Result.success(blogPlatform!!)
        }
        return platformRepo.getPlatform(serverBaseUrl)
            .onSuccess { blogPlatform = it }
    }
}
