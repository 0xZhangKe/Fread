package com.zhangke.utopia.activitypub.app.internal.utils

import com.zhangke.activitypub.entities.ActivityPubPollEntity
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.updateStatus
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.utils.LoadableStatusController
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ActivityPubStatusLoadController(
    private val clientManager: ActivityPubClientManager,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    coroutineScope: CoroutineScope,
    buildStatusUiState: BuildStatusUiStateUseCase,
    private val interactiveHandler: ActivityPubInteractiveHandler,
    private val pollAdapter: ActivityPubPollAdapter,
    private val updateStatus: (ActivityPubStatusEntity) -> Unit = {},
    private val updatePoll: (Status, ActivityPubPollEntity) -> Unit = { _, _ -> },
) : LoadableStatusController(
    coroutineScope = coroutineScope,
    interactiveHandler = null,
    buildStatusUiState = buildStatusUiState,
) {

    fun initStatusData(
        role: IdentityRole,
        getStatusFromServer: suspend (role: IdentityRole) -> Result<List<ActivityPubStatusEntity>>,
        getStatusFromLocal: (suspend () -> List<ActivityPubStatusEntity>)? = null,
    ) {
        initData(
            getDataFromServer = transformRefresh(role, getStatusFromServer),
            getDataFromLocal = transform(role, getStatusFromLocal),
        )
    }

    fun onRefresh(
        role: IdentityRole,
        getStatusFromServer: suspend (role: IdentityRole) -> Result<List<ActivityPubStatusEntity>>,
    ) {
        onRefresh(
            refreshFunction = transformRefresh(role, getStatusFromServer),
        )
    }

    fun onLoadMore(
        role: IdentityRole,
        loadMoreFunction: suspend (maxId: String, IdentityRole) -> Result<List<ActivityPubStatusEntity>>,
    ) {
        onLoadMore(
            loadMoreFunction = transformLoadMore(role, loadMoreFunction),
        )
    }

    override fun onInteractive(
        role: IdentityRole,
        status: Status,
        uiInteraction: StatusUiInteraction,
    ) {
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

    override fun onVoted(role: IdentityRole, status: Status, votedOption: List<BlogPoll.Option>) {
        coroutineScope.launch {
            clientManager.getClient(role)
                .statusRepo
                .votes(
                    id = status.intrinsicBlog.poll!!.id,
                    choices = votedOption.map { it.index },
                )
                .onSuccess { pollEntity ->
                    val poll = pollAdapter.adapt(pollEntity)
                    mutableUiState.update { state ->
                        val dataList = state.dataList.map { itemState ->
                            if (itemState.status.id == status.id) {
                                val newItemStatus = when (val itemStatus = itemState.status) {
                                    is Status.NewBlog -> {
                                        itemStatus.copy(blog = itemStatus.blog.copy(poll = poll))
                                    }

                                    is Status.Reblog -> {
                                        itemStatus.copy(reblog = itemStatus.reblog.copy(poll = poll))
                                    }
                                }
                                itemState.copy(status = newItemStatus)
                            } else {
                                itemState
                            }
                        }
                        state.copyObject(dataList = dataList)
                    }
                    updatePoll(status, pollEntity)
                }.onFailure { e ->
                    e.message?.let { textOf(it) }?.let { mutableErrorMessageFlow.emit(it) }
                }
        }
    }

    private fun transformRefresh(
        role: IdentityRole,
        block: suspend (role: IdentityRole) -> Result<List<ActivityPubStatusEntity>>,
    ): suspend () -> Result<List<Status>> {
        return {
            val result = platformRepo.getPlatform(role)
            if (result.isFailure) {
                Result.failure(result.exceptionOrNull()!!)
            } else {
                val platform = result.getOrNull()!!
                block(role).map { list ->
                    list.map { statusAdapter.toStatus(it, platform) }
                }
            }
        }
    }

    private fun transformLoadMore(
        role: IdentityRole,
        block: suspend (String, IdentityRole) -> Result<List<ActivityPubStatusEntity>>,
    ): suspend (String) -> Result<List<Status>> {
        return { maxId ->
            val result = platformRepo.getPlatform(role)
            if (result.isFailure) {
                Result.failure(result.exceptionOrNull()!!)
            } else {
                val platform = result.getOrNull()!!
                block(maxId, role).map { list ->
                    list.map { statusAdapter.toStatus(it, platform) }
                }
            }
        }
    }

    private fun transform(
        role: IdentityRole,
        block: (suspend () -> List<ActivityPubStatusEntity>)?,
    ): (suspend () -> List<Status>)? {
        if (block == null) return null
        return {
            val result = platformRepo.getPlatform(role)
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
