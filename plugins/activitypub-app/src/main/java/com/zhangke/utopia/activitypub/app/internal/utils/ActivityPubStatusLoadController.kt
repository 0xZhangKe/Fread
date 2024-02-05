package com.zhangke.utopia.activitypub.app.internal.utils

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.updateStatus
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.utils.LoadableStatusController
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActivityPubStatusLoadController(
    private val statusAdapter: ActivityPubStatusAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    coroutineScope: CoroutineScope,
    buildStatusUiState: BuildStatusUiStateUseCase,
    private val interactiveHandler: ActivityPubInteractiveHandler,
    private val updateStatus: (ActivityPubStatusEntity) -> Unit = {},
) : LoadableStatusController(
    coroutineScope = coroutineScope,
    interactiveHandler = null,
    buildStatusUiState = buildStatusUiState,
) {

    fun initStatusData(
        baseUrl: FormalBaseUrl,
        getStatusFromServer: suspend () -> Result<List<ActivityPubStatusEntity>>,
        getStatusFromLocal: (suspend () -> List<ActivityPubStatusEntity>)? = null,
    ) {
        initData(
            getDataFromServer = transform(baseUrl, getStatusFromServer),
            getDataFromLocal = transform(baseUrl, getStatusFromLocal),
        )
    }

    fun onRefresh(
        baseUrl: FormalBaseUrl,
        getStatusFromServer: suspend () -> Result<List<ActivityPubStatusEntity>>,
    ) {
        onRefresh(
            refreshFunction = transform(baseUrl, getStatusFromServer),
        )
    }

    fun onLoadMore(
        baseUrl: FormalBaseUrl,
        loadMoreFunction: suspend (maxId: String) -> Result<List<ActivityPubStatusEntity>>,
    ) {
        onLoadMore(
            loadMoreFunction = transform(baseUrl, loadMoreFunction),
        )
    }

    override fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        coroutineScope.launch {
            when (val result = interactiveHandler.onStatusInteractive(status, uiInteraction)) {
                is ActivityPubInteractiveHandleResult.ShowErrorMessage -> {
                    mutableErrorMessageFlow.emit(result.message)
                }

                is ActivityPubInteractiveHandleResult.UpdateStatus -> {
                    mutableUiState.update {
                        it.copyObject(
                            dataList = it.dataList.updateStatus(result.status),
                        )
                    }
                    updateStatus(result.statusEntity)
                }

                is ActivityPubInteractiveHandleResult.NoOp -> {}
            }
        }
    }

    private fun transform(
        baseUrl: FormalBaseUrl,
        block: suspend () -> Result<List<ActivityPubStatusEntity>>,
    ): suspend () -> Result<List<Status>> {
        return {
            val result = platformRepo.getPlatform(baseUrl)
            if (result.isFailure) {
                Result.failure(result.exceptionOrNull()!!)
            } else {
                val platform = result.getOrNull()!!
                block().map { list ->
                    list.map { statusAdapter.toStatus(it, platform) }
                }
            }
        }
    }

    private fun transform(
        baseUrl: FormalBaseUrl,
        block: suspend (String) -> Result<List<ActivityPubStatusEntity>>,
    ): suspend (String) -> Result<List<Status>> {
        return { maxId ->
            val result = platformRepo.getPlatform(baseUrl)
            if (result.isFailure) {
                Result.failure(result.exceptionOrNull()!!)
            } else {
                val platform = result.getOrNull()!!
                block(maxId).map { list ->
                    list.map { statusAdapter.toStatus(it, platform) }
                }
            }
        }
    }

    private fun transform(
        baseUrl: FormalBaseUrl,
        block: (suspend () -> List<ActivityPubStatusEntity>)?,
    ): (suspend () -> List<Status>)? {
        if (block == null) return null
        return {
            val result = platformRepo.getPlatform(baseUrl)
            if (result.isFailure) {
                emptyList()
            } else {
                val platform = result.getOrNull()!!
                block().map {
                    statusAdapter.toStatus(it, platform)
                }
            }
        }
    }
}
