package com.zhangke.fread.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.coroutines.invokeOnCancel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubStatusReadStateRepo
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.config.FreadConfigManager
import com.zhangke.fread.common.config.TimelineDefaultPosition
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.richtext.preParseStatus
import com.zhangke.fread.status.richtext.preParseStatusList
import com.zhangke.fread.status.status.model.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ActivityPubTimelineViewModel(
    private val statusProvider: StatusProvider,
    statusUpdater: StatusUpdater,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val statusAdapter: ActivityPubStatusAdapter,
    refactorToNewStatus: RefactorToNewStatusUseCase,
    private val timelineRepo: ActivityPubTimelineStatusRepo,
    private val statusReadStateRepo: ActivityPubStatusReadStateRepo,
    private val accountManager: ActivityPubAccountManager,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val freadConfigManager: FreadConfigManager,
    private val locator: PlatformLocator,
    private val type: ActivityPubStatusSourceType,
    private val listId: String?,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    private val _uiState = MutableStateFlow(ActivityPubTimelineUiState.default())
    val uiState = _uiState.asStateFlow()

    private var initFeedsJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null
    private var loadPreviousJob: Job? = null

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { result ->
                result.handle()
            }
        )
        initFeeds()
    }

    private fun initFeeds() {
        // 1. load local first page(100 item) data
        // 2. load previous page of this local page data from server
        // 3. position item of latest read item, if can't position, move to top.
        initFeedsJob?.cancel()
        initFeedsJob = launchInViewModel {
            _uiState.update {
                it.copy(
                    items = emptyList(),
                    showPagingLoadingPlaceholder = true,
                )
            }
            val account = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
            val minId = maybeLoadLocalFeeds(account)
            if (minId.isNullOrEmpty()) {
                timelineRepo.getFresherStatus(
                    locator = locator,
                    type = type,
                    listId = listId,
                )
            } else {
                timelineRepo.loadPreviousPageStatus(
                    locator = locator,
                    type = type,
                    minId = minId,
                    listId = listId,
                )
            }.map {
                it.preParseStatusList()
                it.toTimelineItems(account)
            }.onFailure { t ->
                if (_uiState.value.items.isEmpty()) {
                    _uiState.update {
                        it.copy(
                            showPagingLoadingPlaceholder = false,
                            pageErrorContent = t.toTextStringOrNull(),
                        )
                    }
                } else {
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(t)
                }
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        items = list.appendItems(it.items),
                        showPagingLoadingPlaceholder = false,
                    )
                }
            }
        }
        initFeedsJob!!.invokeOnCancel {
            _uiState.update {
                it.copy(showPagingLoadingPlaceholder = false)
            }
        }
    }

    /**
     * @return minId
     */
    private suspend fun maybeLoadLocalFeeds(
        account: ActivityPubLoggedAccount?,
    ): String? {
        if (freadConfigManager.getTimelineDefaultPosition() == TimelineDefaultPosition.NEWEST) return null
        val localStatus =
            if (type == ActivityPubStatusSourceType.TIMELINE_LOCAL || type == ActivityPubStatusSourceType.TIMELINE_PUBLIC) {
                emptyList()
            } else {
                timelineRepo.getStatusFromLocal(
                    locator = locator,
                    type = type,
                    listId = listId,
                ).map {
                    it.preParseStatus()
                    it
                }
            }
        if (localStatus.isNotEmpty()) {
            val latestReadStatus = statusReadStateRepo.getLatestReadId(locator, type, listId)
            val initialIndex = localStatus.indexOfFirst { it.id == latestReadStatus }
            _uiState.update {
                it.copy(
                    items = localStatus.toTimelineItems(account),
                    initialShowIndex = if (initialIndex in localStatus.indices) initialIndex else 0,
                    showPagingLoadingPlaceholder = false,
                )
            }
        }
        return localStatus.firstOrNull()?.id
    }

    fun onRefresh() {
        loadPreviousJob?.cancel()
        loadMoreJob?.cancel()
        refreshJob?.cancel()
        refreshJob = launchInViewModel {
            _uiState.update { it.copy(refreshing = true) }
            val account = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
            timelineRepo.getFresherStatus(
                locator = locator,
                type = type,
                listId = listId,
            ).map {
                it.preParseStatusList()
                it.toTimelineItems(account)
            }.onFailure { t ->
                _uiState.update { it.copy(refreshing = false) }
                mutableErrorMessageFlow.emitTextMessageFromThrowable(t)
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        items = list,
                        refreshing = false,
                        jumpToStatusId = list.firstOrNull()?.statusId,
                    )
                }
            }
        }
        refreshJob?.invokeOnCancel {
            _uiState.update { it.copy(refreshing = false) }
        }
    }

    fun onJumpedToStatus() {
        _uiState.update { it.copy(jumpToStatusId = null) }
    }

    fun onLoadPreviousPage() {
        if (refreshJob?.isActive == true) return
        if (initFeedsJob?.isActive == true) return
        if (loadPreviousJob?.isActive == true) return
        val minId = uiState.value.items.getStatusIdOrNull(0) ?: return
        val account = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
        loadPreviousJob = launchInViewModel {
            timelineRepo.loadPreviousPageStatus(
                locator = locator,
                type = type,
                minId = minId,
                listId = listId,
            ).map {
                it.preParseStatusList()
                it.toTimelineItems(account)
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        items = list.appendItems(it.items),
                    )
                }
            }
        }
    }

    fun onLoadMore() {
        val maxId = uiState.value
            .items
            .lastOrNull { it is ActivityPubTimelineItem.StatusItem }
            ?.let { it as ActivityPubTimelineItem.StatusItem }
            ?.status
            ?.status
            ?.id ?: return
        loadMoreJob?.cancel()
        loadMoreJob = launchInViewModel {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            val account = locator.accountUri?.let { loggedAccountProvider.getAccount(it) }
            timelineRepo.loadMore(
                locator = locator,
                type = type,
                maxId = maxId,
                listId = listId,
            ).map {
                it.preParseStatusList()
                it.toTimelineItems(account)
            }.onFailure { t ->
                _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        items = it.items.appendItems(list),
                        loadMoreState = LoadState.Idle,
                    )
                }
            }
        }
        loadMoreJob!!.invokeOnCancel {
            _uiState.update { it.copy(loadMoreState = LoadState.Idle) }
        }
    }

    fun updateMaxReadStatus(item: ActivityPubTimelineItem) {
        val statusId = (item as ActivityPubTimelineItem.StatusItem).status.status.id
        launchInViewModel {
            statusReadStateRepo.updateLatestReadId(
                locator = locator,
                type = type,
                listId = listId,
                latestReadId = statusId,
            )
        }
    }

    private suspend fun InteractiveHandleResult.handle() {
        when (this) {
            is InteractiveHandleResult.UpdateStatus -> {
                _uiState.update { state ->
                    state.copy(items = state.items.updateStatus(this.status))
                }
                timelineRepo.updateStatus(
                    locator = locator,
                    type = type,
                    status = this.status.status,
                    listId = listId,
                )
            }

            is InteractiveHandleResult.DeleteStatus -> {
                _uiState.update { state ->
                    state.copy(
                        items = state.items.filter {
                            when (it) {
                                is ActivityPubTimelineItem.StatusItem -> it.status.status.id != this.statusId
                            }
                        }
                    )
                }
                timelineRepo.deleteStatus(statusId)
            }

            is InteractiveHandleResult.UpdateFollowState -> {}
        }
    }

    private fun List<ActivityPubTimelineItem>.appendItems(
        items: List<ActivityPubTimelineItem>,
    ): List<ActivityPubTimelineItem> {
        if (items.isEmpty()) return this
        val newList = this.toMutableList()
        val idSet = this.map { it.statusId }
        val pendingAddItems = items.filter { it.statusId !in idSet }
        newList.addAll(pendingAddItems)
        return newList
    }

    private val ActivityPubTimelineItem.statusId: String
        get() {
            return when (this) {
                is ActivityPubTimelineItem.StatusItem -> this.status.status.id
            }
        }

    private fun List<Status>.toTimelineItems(
        loggedAccount: ActivityPubLoggedAccount?,
    ): List<ActivityPubTimelineItem> {
        return this.map {
            ActivityPubTimelineItem.StatusItem(
                statusAdapter.toStatusUiState(
                    status = it,
                    locator = locator,
                    loggedAccount = loggedAccount,
                ),
            )
        }
    }
}
