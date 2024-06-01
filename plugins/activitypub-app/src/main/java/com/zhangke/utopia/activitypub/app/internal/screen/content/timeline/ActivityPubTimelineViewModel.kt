package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.composable.LoadPreviousState
import com.zhangke.utopia.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
            ).map { it.toTimelineItems() }
                .onFailure { t ->
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
    }

    fun onLoadPreviousPage() {
        val sinceId = uiState.value
            .items
            .getStatusIdOrNull(0) ?: return
        loadPreviousJob?.cancel()
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            _uiState.update { it.copy(loadPreviousState = LoadPreviousState.Failed(t.toTextStringOrNull())) }
        }
        loadPreviousJob = launchInViewModel(exceptionHandler) {
            _uiState.update { it.copy(loadPreviousState = LoadPreviousState.Loading) }
            timelineRepo.loadPreviousPageStatus(
                role = role,
                type = type,
                sinceId = sinceId,
                listId = listId,
            ).map { it.toTimelineItems() }
                .onFailure { t ->
                    _uiState.update { it.copy(loadPreviousState = LoadPreviousState.Failed(t.toTextStringOrNull())) }
                }.onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            items = list.appendItems(it.items),
                            loadPreviousState = LoadPreviousState.Idle,
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
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
        }
        loadMoreJob = launchInViewModel(exceptionHandler) {
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
