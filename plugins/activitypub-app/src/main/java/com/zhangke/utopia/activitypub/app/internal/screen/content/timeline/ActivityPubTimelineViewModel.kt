package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.distinctByKey
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.db.status.ActivityPubStatusTableEntity
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ActivityPubTimelineViewModel(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    private val timelineRepo: ActivityPubTimelineStatusRepo,
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

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessageFlow.asSharedFlow()

    private var initFeedsJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null
    private var loadPreviousJob: Job? = null
    private var loadFractureJob: Job? = null

    init {
        initFeeds()
    }

    private fun initFeeds() {
        // 1. load local first page(100 item) data
        // 2. load previous page of this local page data from server
        // 3. position item of latest read item, if can't position, move to top.
        initFeedsJob?.cancel()
        initFeedsJob = launchInViewModel {
            _uiState.update { it.copy(showPagingLoadingPlaceholder = true) }
            val localStatus = timelineRepo.getStatusFromLocal(
                role = role,
                type = type,
                listId = listId,
            )
            if (localStatus.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        items = localStatus.toTimelineItems(),
                        showPagingLoadingPlaceholder = false,
                    )
                }
            }
            val sinceId = localStatus.firstOrNull()?.status?.id
            if (sinceId.isNullOrEmpty()) {
                timelineRepo.getStatusFromServer(
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
            }.map { it.toTimelineItems() }
                .onFailure { t ->
                    if (_uiState.value.items.isEmpty()) {
                        _uiState.update {
                            it.copy(
                                showPagingLoadingPlaceholder = false,
                                pageErrorContent = t.toTextStringOrNull(),
                            )
                        }
                    } else {
                        _snackBarMessageFlow.emitTextMessageFromThrowable(t)
                    }
                }.onSuccess { list ->
                    _uiState.update {
                        val newList = list.toMutableList()
                        newList.addAll(it.items)
                        it.copy(
                            items = newList,
                            showPagingLoadingPlaceholder = false,
                        )
                    }
                }
        }
    }

    fun onRefresh() {
        refreshJob?.cancel()
        refreshJob = launchInViewModel {
            _uiState.update { it.copy(refreshing = true) }
            timelineRepo.getStatusFromServer(
                role = role,
                type = type,
                listId = listId,
            ).map { it.toTimelineItems() }
                .onFailure { t ->
                    _uiState.update { it.copy(refreshing = false) }
                    _snackBarMessageFlow.emitTextMessageFromThrowable(t)
                }.onSuccess { list ->
                    val newList = list.toMutableList()
                    newList += _uiState.value.items
                    _uiState.update {
                        it.copy(
                            items = newList,
                            refreshing = false,
                        )
                    }
                }
        }
    }

    fun onLoadPreviousPage() {
        val sinceId = uiState.value
            .items
            .getStatusIdOrNull(0) ?: return
        loadPreviousJob?.cancel()
        loadPreviousJob = launchInViewModel {
            timelineRepo.loadPreviousPageStatus(
                role = role,
                type = type,
                sinceId = sinceId,
                listId = listId,
            ).map { it.toTimelineItems() }
                .onFailure {
                    _snackBarMessageFlow.emitTextMessageFromThrowable(it)
                }.onSuccess { list ->
                    _uiState.update {
                        val newList = list.toMutableList()
                        newList.addAll(it.items)
                        it.copy(
                            items = newList,
                        )
                    }
                }
        }
    }

    fun onFractureClick(index: Int) {
        val items = uiState.value.items
        if (items[index] !is ActivityPubTimelineItem.FractureItem) return
        val maxId = items.getStatusIdOrNull(index - 1) ?: return
        loadFractureJob?.cancel()
        loadPreviousJob = launchInViewModel {
            _uiState.update { it.copy(items = items.updateFractureItem(index, ActivityPubTimelineItem.FractureItem(LoadState.Loading))) }
            timelineRepo.loadFracture(
                role = role,
                type = type,
                maxId = maxId,
                listId = listId,
            ).map { it.toTimelineItems() }
                .onFailure { throwable ->
                    val newFractureItem = ActivityPubTimelineItem.FractureItem(LoadState.Failed(throwable.toTextStringOrNull()))
                    _uiState.update { it.copy(items = items.updateFractureItem(index, newFractureItem)) }
                }.onSuccess { list ->
                    _uiState.update { state ->
                        state.copy(items = state.items.insertItems(index, list))
                    }
                }
        }
    }

    private fun List<ActivityPubTimelineItem>.insertItems(
        index: Int,
        items: List<ActivityPubTimelineItem>,
    ): List<ActivityPubTimelineItem> {
        if (items.isEmpty()) return this
        val newList = this.toMutableList()
        newList.removeAt(index)
        newList.addAll(index, items)
        return newList.distinctByKey { i, item ->
            if (item is ActivityPubTimelineItem.StatusItem) {
                item.status.status.id
            } else {
                i.toString()
            }
        }
    }

    private fun List<ActivityPubTimelineItem>.updateFractureItem(
        index: Int,
        fractureItem: ActivityPubTimelineItem.FractureItem,
    ): List<ActivityPubTimelineItem> {
        if (this[index] !is ActivityPubTimelineItem.FractureItem) return this
        val newList = this.toMutableList()
        newList[index] = fractureItem
        return newList
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
            ).map { it.toTimelineItems() }
                .onFailure { t ->
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
    }

    private fun List<ActivityPubTimelineItem>.appendItems(items: List<ActivityPubTimelineItem>): List<ActivityPubTimelineItem> {
        val newList = this.toMutableList()
        val tailFractureCount = newList.tailFractureCount
        if (tailFractureCount > 0) {
            repeat(tailFractureCount) {
                newList.removeLast()
            }
        }
        newList.addAll(items)
        return newList
    }

    private fun List<ActivityPubStatusTableEntity>.toTimelineItems(): List<ActivityPubTimelineItem> {
        val timelines = mutableListOf<ActivityPubTimelineItem>()
        this.forEach {
            timelines += it.toActivityPubTimelineItem()
            if (it.fracture) {
                timelines += ActivityPubTimelineItem.FractureItem(LoadState.Idle)
            }
        }
        return timelines
    }

    private fun ActivityPubStatusTableEntity.toActivityPubTimelineItem(): ActivityPubTimelineItem {
        return ActivityPubTimelineItem.StatusItem(buildStatusUiState(role, this.status))
    }

    private fun List<Status>.toTimelineItems(): List<ActivityPubTimelineItem> {
        return map { ActivityPubTimelineItem.StatusItem(buildStatusUiState(role, it)) }
    }

    private fun Status.toTimelineItem(): ActivityPubTimelineItem {
        return ActivityPubTimelineItem.StatusItem(buildStatusUiState(role, this))
    }
}
