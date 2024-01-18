package com.zhangke.utopia.activitypub.app.internal.screen.content

import android.util.Log
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
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

abstract class StatusViewModel(
    private val platformRepo: ActivityPubPlatformRepo,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    protected val serverBaseUrl: FormalBaseUrl,
) : SubViewModel() {

    abstract suspend fun getLocalStatus(): List<ActivityPubStatusEntity>

    abstract suspend fun getRemoteStatus(): Result<List<ActivityPubStatusEntity>>

    abstract suspend fun loadMore(maxId: String): Result<List<ActivityPubStatusEntity>>

    private val _uiState = MutableStateFlow(
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
            Log.d("U_TEST", "statusFromLocal: ${statusFromLocal.joinToString(",") { it.id }}")
            refreshStatus(false)
        }
    }

    fun onRefresh() {
        launchInViewModel {
            refreshStatus(true)
        }
    }

    private suspend fun refreshStatus(showRefreshing: Boolean) {
        updateRefreshState(showRefreshing, true)
        val platformResult = getBlogPlatform()
        if (platformResult.isFailure) {
            updateRefreshState(showRefreshing, false)
            _snackMessage.emit(textOf(platformResult.exceptionOrNull()!!.message.orEmpty()))
            return
        }
        val platform = platformResult.getOrThrow()
        getRemoteStatus().onSuccess {
            _uiState.value = _uiState.value.copy(
                status = it.toUiStates(platform),
                refreshing = false,
            )
            Log.d("U_TEST", "refreshStatus: ${it.joinToString(",") { it.id }}")
        }.onFailure {
            updateRefreshState(showRefreshing, false)
            _snackMessage.emit(textOf(it.message.orEmpty()))
        }
    }

    private fun updateRefreshState(showRefreshing: Boolean, refreshing: Boolean) {
        _uiState.value = _uiState.value.copy(refreshing = showRefreshing && refreshing)
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
                    Log.d("U_TEST", "onLoadMore(${latestStatus.status.id}): ${it.joinToString(",") { it.id }}")
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
