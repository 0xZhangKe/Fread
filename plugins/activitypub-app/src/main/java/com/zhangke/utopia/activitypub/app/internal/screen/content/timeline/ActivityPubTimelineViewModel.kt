package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import android.util.Log
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.coroutines.invokeOnCancel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { result ->
                result.handle()
            }
        )
        initFeeds()
    }

    private fun initFeeds() {
        Log.d("U_TEST", "TimelineVM: initFeeds")
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
            Log.d("U_TEST", "TimelineVM: initFeeds localStatus: ${localStatus.size}")
            if (localStatus.isNotEmpty()) {
                Log.d("U_TEST", "TimelineVM: initFeeds localStatus: ${localStatus.size}")
                localStatus.joinToString { it.id }?.let {
                    Log.d("U_TEST", "TimelineVM: initFeeds localStatus: ${it}")
                }
                _uiState.update {
                    it.copy(
                        items = localStatus.toTimelineItems(),
                        showPagingLoadingPlaceholder = false,
                    )
                }
            }
            val sinceId = localStatus.firstOrNull()?.id
            if (sinceId.isNullOrEmpty()) {
                Log.d("U_TEST", "TimelineVM: initFeeds getFresherStatus")
                timelineRepo.getFresherStatus(
                    role = role,
                    type = type,
                    listId = listId,
                )
            } else {
                Log.d("U_TEST", "TimelineVM: initFeeds loadPreviousPageStatus: $sinceId")
                timelineRepo.loadPreviousPageStatus(
                    role = role,
                    type = type,
                    sinceId = sinceId,
                    listId = listId,
                )
            }.map { it.toTimelineItems() }
                .onFailure { t ->
                    Log.d("U_TEST", "TimelineVM: initFeeds onFailure: $t")
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
                    list.joinToString { (it as ActivityPubTimelineItem.StatusItem).status.status.id }
                        .let {
                            Log.d("U_TEST", "TimelineVM: initFeeds onSuccess: $it")
                        }
                    _uiState.update {
                        it.copy(
                            items = list.appendItems(it.items),
                            showPagingLoadingPlaceholder = false,
                        )
                    }
                }
        }
        initFeedsJob!!.invokeOnCancel { t ->
            Log.d("U_TEST", "TimelineVM: initFeeds invokeOnCompletion: $t")
            _uiState.update {
                it.copy(showPagingLoadingPlaceholder = false)
            }
        }
    }

    fun onRefresh() {
        Log.d("U_TEST", "TimelineVM: onRefresh")
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
                    Log.d("U_TEST", "TimelineVM: onRefresh onFailure: $t")
                    _uiState.update { it.copy(refreshing = false) }
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(t)
                }.onSuccess { list ->
                    list.joinToString { (it as ActivityPubTimelineItem.StatusItem).status.status.id }
                        .let {
                            Log.d("U_TEST", "TimelineVM: onRefresh onSuccess: $it")
                        }
                    _uiState.update {
                        it.copy(
                            items = list,
                            refreshing = false,
                        )
                    }
                }
        }
        refreshJob?.invokeOnCancel {
            Log.d("U_TEST", "TimelineVM: onRefresh invokeOnCompletion: $it")
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
        Log.d("U_TEST", "TimelineVM: onLoadPreviousPage($sinceId)")
        loadPreviousJob = launchInViewModel {
            timelineRepo.loadPreviousPageStatus(
                role = role,
                type = type,
                sinceId = sinceId,
                listId = listId,
            ).map { it.toTimelineItems() }
                .onFailure { t ->
                    Log.d("U_TEST", "TimelineVM: onLoadPreviousPage($sinceId) onFailure: $t")
                }.onSuccess { list ->
                    list.joinToString { (it as ActivityPubTimelineItem.StatusItem).status.status.id }
                        .let {
                            Log.d("U_TEST", "TimelineVM: onLoadPreviousPage($sinceId) onSuccess: $it")
                        }
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
        Log.d("U_TEST", "TimelineVM: onLoadMore($maxId)")
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
                    Log.d("U_TEST", "TimelineVM: onLoadMore($maxId) onFailure: $t")
                    _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
                }.onSuccess { list ->
                    list.joinToString { (it as ActivityPubTimelineItem.StatusItem).status.status.id }
                        .let {
                            Log.d("U_TEST", "TimelineVM: onLoadMore($maxId) onSuccess: $it")
                        }
                    _uiState.update {
                        it.copy(
                            items = it.items.appendItems(list),
                            loadMoreState = LoadState.Idle,
                        )
                    }
                }
        }
        loadMoreJob!!.invokeOnCancel { t ->
            Log.d("U_TEST", "TimelineVM: LoadMore($maxId) invokeOnCompletion: $t")
            _uiState.update { it.copy(loadMoreState = LoadState.Idle) }
        }
    }

    private fun InteractiveHandleResult.handle() {
        when (this) {
            is InteractiveHandleResult.UpdateStatus -> {
                _uiState.update { state ->
                    state.copy(items = state.items.updateStatus(this.status))
                }
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
