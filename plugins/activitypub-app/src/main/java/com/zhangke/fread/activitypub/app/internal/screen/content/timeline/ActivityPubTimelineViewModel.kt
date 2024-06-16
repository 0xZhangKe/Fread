package com.zhangke.fread.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.coroutines.invokeOnCancel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubLoggedAccount
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubStatusReadStateRepo
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.richtext.preParseRichText
import com.zhangke.fread.status.status.model.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Auto delete expired status
// Double top to scroll to top
// pull to refresh to load previous page
class ActivityPubTimelineViewModel(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    private val timelineRepo: ActivityPubTimelineStatusRepo,
    private val statusReadStateRepo: ActivityPubStatusReadStateRepo,
    private val accountManager: ActivityPubAccountManager,
    private val role: IdentityRole,
    private val type: ActivityPubStatusSourceType,
    private val listId: String?,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val _uiState = MutableStateFlow(ActivityPubTimelineUiState.default)
    val uiState = _uiState.asStateFlow()

    private var initFeedsJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null
    private var loadPreviousJob: Job? = null

    private var latestAccount: ActivityPubLoggedAccount? = null

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { result ->
                result.handle()
            }
        )
        initFeeds()
        launchInViewModel {
            val baseUrl = role.baseUrl
            if (baseUrl != null) {
                accountManager.observeAccount(baseUrl)
                    .collect {
                        if (latestAccount != null && latestAccount?.uri == it?.uri) {
                            return@collect
                        }
                        latestAccount = it
                        initFeeds()
                    }
            }
        }
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
            val localStatus = timelineRepo.getStatusFromLocal(
                role = role,
                type = type,
                listId = listId,
            ).map {
                it.preParseRichText()
                it
            }
            if (localStatus.isNotEmpty()) {
                val latestReadStatus = statusReadStateRepo.getLatestReadId(role, type, listId)
                val initialIndex = localStatus.indexOfFirst { it.id == latestReadStatus }
                _uiState.update {
                    it.copy(
                        items = localStatus.toTimelineItems(),
                        initialShowIndex = if (initialIndex in localStatus.indices) initialIndex else 0,
                        showPagingLoadingPlaceholder = false,
                    )
                }
            }
            val sinceId = localStatus.firstOrNull()?.id
            if (sinceId.isNullOrEmpty()) {
                timelineRepo.getFresherStatus(
                    role = role,
                    type = type,
                    listId = listId,
                )
            } else {
                timelineRepo.loadPreviousPageStatus(
                    role = role,
                    type = type,
                    sinceId = sinceId,
                    listId = listId,
                )
            }.map {
                it.preParseRichText()
                it.toTimelineItems()
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
        initFeedsJob!!.invokeOnCancel { t ->
            _uiState.update {
                it.copy(showPagingLoadingPlaceholder = false)
            }
        }
    }

    fun onRefresh() {
        loadPreviousJob?.cancel()
        loadMoreJob?.cancel()
        refreshJob?.cancel()
        refreshJob = launchInViewModel {
            _uiState.update { it.copy(refreshing = true) }
            timelineRepo.getFresherStatus(
                role = role,
                type = type,
                listId = listId,
            ).map {
                it.preParseRichText()
                it.toTimelineItems()
            }.onFailure { t ->
                _uiState.update { it.copy(refreshing = false) }
                mutableErrorMessageFlow.emitTextMessageFromThrowable(t)
            }.onSuccess { list ->
                _uiState.update {
                    it.copy(
                        items = list,
                        refreshing = false,
                    )
                }
            }
        }
        refreshJob?.invokeOnCancel {
            _uiState.update { it.copy(refreshing = false) }
        }
    }

    fun onLoadPreviousPage() {
        if (refreshJob?.isActive == true) return
        if (initFeedsJob?.isActive == true) return
        if (loadPreviousJob?.isActive == true) return
        val sinceId = uiState.value
            .items
            .getStatusIdOrNull(0) ?: return
        loadPreviousJob = launchInViewModel {
            timelineRepo.loadPreviousPageStatus(
                role = role,
                type = type,
                sinceId = sinceId,
                listId = listId,
            ).map {
                it.preParseRichText()
                it.toTimelineItems()
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
            timelineRepo.loadMore(
                role = role,
                type = type,
                maxId = maxId,
                listId = listId,
            ).map {
                it.preParseRichText()
                it.toTimelineItems()
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
        loadMoreJob!!.invokeOnCancel { t ->
            _uiState.update { it.copy(loadMoreState = LoadState.Idle) }
        }
    }

    fun updateMaxReadStatus(item: ActivityPubTimelineItem) {
        val statusId = (item as ActivityPubTimelineItem.StatusItem).status.status.id
        launchInViewModel {
            statusReadStateRepo.updateLatestReadId(
                role = role,
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
                    role = role,
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
        newList.addAll(items)
        return newList
    }

    private fun List<Status>.toTimelineItems(): List<ActivityPubTimelineItem> {
        return map { ActivityPubTimelineItem.StatusItem(buildStatusUiState(role, it)) }
    }
}
