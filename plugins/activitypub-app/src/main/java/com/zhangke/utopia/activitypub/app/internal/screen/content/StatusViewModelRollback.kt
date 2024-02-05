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
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.usecase.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.handle
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 一个用来加载 Status 列表的 BaseViewModel。
 * 具体的加载细节会交给子类实现。
 * 该类主要包含 UiState 管理，刷新和加载更多的逻辑。
 */
abstract class StatusViewModelRollback(
    private val platformRepo: ActivityPubPlatformRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val statusInteractive: StatusInteractiveUseCase,
    protected val serverBaseUrl: FormalBaseUrl,
    private val statusInteractiveHandler: InteractiveHandler,
) : SubViewModel() {

    abstract suspend fun getLocalStatus(): List<ActivityPubStatusEntity>

    abstract suspend fun getRemoteStatus(): Result<List<ActivityPubStatusEntity>>

    abstract suspend fun loadMore(maxId: String): Result<List<ActivityPubStatusEntity>>

    abstract suspend fun updateLocalStatus(status: ActivityPubStatusEntity)

    protected val mutableUiState = MutableStateFlow(
        FeedsStatusUiState(
            status = emptyList(),
            refreshing = false,
            loadMoreState = LoadState.Idle,
            errorMessage = null,
        )
    )
    val uiState: StateFlow<FeedsStatusUiState> = mutableUiState.asStateFlow()

    private val _snackMessage = MutableSharedFlow<TextString>()
    val snackMessage: SharedFlow<TextString> = _snackMessage.asSharedFlow()

    private var blogPlatform: BlogPlatform? = null

    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    protected fun prepare() {
        launchInViewModel {
            val statusFromLocal = getLocalStatus()
            val platformResult = getBlogPlatform()
            if (platformResult.isFailure) {
                mutableUiState.value = mutableUiState.value.copy(
                    errorMessage = textOf(platformResult.exceptionOrNull()!!.message.orEmpty()),
                )
                return@launchInViewModel
            }
            val platform = platformResult.getOrThrow()
            mutableUiState.value = mutableUiState.value.copy(
                status = statusFromLocal.toUiStates(platform),
            )
            refreshStatus(statusFromLocal.isEmpty())
        }
    }

    fun onRefresh() {
        refreshJob?.cancel()
        refreshJob = launchInViewModel {
            refreshStatus(true)
        }
    }

    private suspend fun refreshStatus(showRefreshing: Boolean) {
        if (mutableUiState.value.refreshing || mutableUiState.value.loadMoreState == LoadState.Loading) return
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
            mutableUiState.value = mutableUiState.value.copy(
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
        errorMessage: TextString? = mutableUiState.value.errorMessage,
    ) {
        mutableUiState.value = mutableUiState.value.copy(
            refreshing = showRefreshing && refreshing,
            errorMessage = errorMessage,
        )
    }

    fun onLoadMore() {
        if (mutableUiState.value.refreshing || mutableUiState.value.loadMoreState == LoadState.Loading) return
        val latestStatus = mutableUiState.value.status.lastOrNull() ?: return
        loadMoreJob?.cancel()
        loadMoreJob = launchInViewModel {
            mutableUiState.value = mutableUiState.value.copy(
                loadMoreState = LoadState.Loading,
            )
            val platformResult = getBlogPlatform()
            if (platformResult.isFailure) {
                mutableUiState.value = mutableUiState.value.copy(
                    loadMoreState = LoadState.Failed(platformResult.exceptionOrNull()!!),
                )
                return@launchInViewModel
            }
            val platform = platformResult.getOrThrow()
            loadMore(maxId = latestStatus.status.id)
                .onSuccess {
                    val currentState = mutableUiState.value
                    mutableUiState.value = currentState.copy(
                        status = currentState.status + it.toUiStates(platform),
                        loadMoreState = LoadState.Idle,
                    )
                }.onFailure {
                    mutableUiState.value = mutableUiState.value.copy(
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
                    mutableUiState.update { current ->
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
