package com.zhangke.utopia.activitypub.app.internal.screen.lists

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.platform.BlogPlatform
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

class ActivityPubListStatusSubViewModel(
    private val clientManager: ActivityPubClientManager,
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
            refreshState = LoadState.Idle,
            loadMoreState = LoadState.Idle,
        )
    )
    val uiState: StateFlow<ActivityPubListStatusUiState> = _uiState.asStateFlow()

    private val _snackMessage = MutableSharedFlow<TextString>()
    val snackMessage: SharedFlow<TextString> = _snackMessage.asSharedFlow()

    private var blogPlatform: BlogPlatform? = null

    init {
        launchInViewModel {
            _uiState.value = _uiState.value.copy(
                refreshState = LoadState.Loading,
            )
            val statusFromLocal = listStatusRepo.getLocalStatus(
                serverBaseUrl = serverBaseUrl,
                listId = listId,
            )
            val platformResult = getBlogPlatform()
            if (platformResult.isFailure) {
                _uiState.value = _uiState.value.copy(
                    refreshState = LoadState.Failed(platformResult.exceptionOrNull()!!),
                )
                return@launchInViewModel
            }
            val platform = platformResult.getOrThrow()
            _uiState.value = _uiState.value.copy(
                status = statusFromLocal.toUiStates(platform),
                refreshState = LoadState.Idle,
            )
            listStatusRepo.getRemoteStatus(
                serverBaseUrl = serverBaseUrl,
                listId = listId,
            ).onSuccess {
                _uiState.value = _uiState.value.copy(
                    status = it.toUiStates(platform),
                )
            }.onFailure {
                _snackMessage.emit(textOf(it.message ?: ""))
            }
        }
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
