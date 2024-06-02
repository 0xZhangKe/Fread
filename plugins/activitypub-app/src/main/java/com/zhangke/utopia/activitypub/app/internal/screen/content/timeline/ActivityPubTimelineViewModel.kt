package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import android.util.Log
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.loadable.previous.PreviousPageLoadingState
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
        initFeeds()
    }

    private fun initFeeds() {
        Log.d("U_TEST", "TimelineVM: initFeeds")
        // 1. load local first page(100 item) data
        // 2. load previous page of this local page data from server
        // 3. position item of latest read item, if can't position, move to top.
        initFeedsJob?.cancel()
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            Log.d("U_TEST", "TimelineVM: initFeeds exceptionHandler: $t")
            _uiState.update { it.copy(showPagingLoadingPlaceholder = false) }
        }
        initFeedsJob = createScope(exceptionHandler).launch {
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
                    Log.d("U_TEST", "TimelineVM: initFeeds onSuccess: ${list.size}")
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
    }

    fun onRefresh() {
        Log.d("U_TEST", "TimelineVM: onRefresh")
        loadPreviousJob?.cancel()
        loadMoreJob?.cancel()
        refreshJob?.cancel()
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            Log.d("U_TEST", "TimelineVM: onRefresh exceptionHandler: $t")
            _uiState.update { it.copy(refreshing = false) }
        }
        refreshJob = createScope(exceptionHandler).launch {
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
                    Log.d("U_TEST", "TimelineVM: onRefresh onSuccess: ${list.size}")
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
    }

    fun onLoadPreviousPage() {
        Log.d("U_TEST", "TimelineVM: onLoadPreviousPage")
        if (refreshJob?.isActive == true) return
        if (initFeedsJob?.isActive == true) return
        val sinceId = uiState.value
            .items
            .getStatusIdOrNull(0) ?: return
        loadPreviousJob?.cancel()
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            Log.d("U_TEST", "TimelineVM: onLoadPreviousPage exceptionHandler: $t")
            _uiState.update { it.copy(loadPreviousState = PreviousPageLoadingState.Failed(t.toTextStringOrNull())) }
        }
        loadPreviousJob = createScope(exceptionHandler).launch {
            _uiState.update { it.copy(loadPreviousState = PreviousPageLoadingState.Loading) }
            timelineRepo.loadPreviousPageStatus(
                role = role,
                type = type,
                sinceId = sinceId,
                listId = listId,
            ).map { it.toTimelineItems() }
                .onFailure { t ->
                    Log.d("U_TEST", "TimelineVM: onLoadPreviousPage onFailure: $t")
                    _uiState.update { it.copy(loadPreviousState = PreviousPageLoadingState.Failed(t.toTextStringOrNull())) }
                }.onSuccess { list ->
                    Log.d("U_TEST", "TimelineVM: onLoadPreviousPage onSuccess: ${list.size}")
                    list.joinToString { (it as ActivityPubTimelineItem.StatusItem).status.status.id }
                        .let {
                            Log.d("U_TEST", "TimelineVM: onRefresh onSuccess: $it")
                        }
                    _uiState.update {
                        it.copy(
                            items = list.appendItems(it.items),
                            loadPreviousState = PreviousPageLoadingState.Idle,
                        )
                    }
                }
        }
    }

    fun onLoadMore() {
        Log.d("U_TEST", "TimelineVM: onLoadMore")
        val maxId = uiState.value
            .items
            .lastOrNull { it is ActivityPubTimelineItem.StatusItem }
            ?.let { it as ActivityPubTimelineItem.StatusItem }
            ?.status
            ?.status
            ?.id ?: return
        loadMoreJob?.cancel()
        val exceptionHandler = CoroutineExceptionHandler { _, t ->
            Log.d("U_TEST", "TimelineVM: onLoadMore exceptionHandler: $t")
            _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
        }
        loadMoreJob = createScope(exceptionHandler).launch {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            timelineRepo.loadMore(
                role = role,
                type = type,
                maxId = maxId,
                listId = listId,
            ).map { it.toTimelineItems() }
                .onFailure { t ->
                    Log.d("U_TEST", "TimelineVM: onLoadMore onFailure: $t")
                    _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
                }.onSuccess { list ->
                    Log.d("U_TEST", "TimelineVM: onLoadMore onSuccess: ${list.size}")
                    list.joinToString { (it as ActivityPubTimelineItem.StatusItem).status.status.id }
                        .let {
                            Log.d("U_TEST", "TimelineVM: onRefresh onSuccess: $it")
                        }
                    _uiState.update {
                        it.copy(
                            items = it.items.appendItems(list),
                            loadMoreState = LoadState.Idle,
                        )
                    }
                }
        }
    }

    private fun createScope(handler: CoroutineExceptionHandler): CoroutineScope {
        return CoroutineScope(Dispatchers.Main + handler)
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
