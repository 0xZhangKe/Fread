package com.zhangke.utopia.commonbiz.shared.feeds

import android.util.Log
import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.HashtagInStatus
import com.zhangke.utopia.status.model.Mention
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedsViewModelController(
    statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : IFeedsViewModelController {

    private lateinit var coroutineScope: CoroutineScope
    private lateinit var loadFirstPageLocalFeeds: suspend () -> Result<List<Status>>
    private lateinit var loadNewFromServerFunction: suspend () -> Result<RefreshResult>
    private lateinit var loadMoreFunction: suspend (maxId: String) -> Result<List<Status>>
    private lateinit var onStatusUpdate: suspend (Status) -> Unit

    private val interactiveHandler = InteractiveHandler(
        statusProvider = statusProvider,
        buildStatusUiState = buildStatusUiState,
    )

    override val mutableUiState = MutableStateFlow(
        CommonFeedsUiState(
            feeds = emptyList(),
            showPagingLoadingPlaceholder = false,
            pageErrorContent = null,
            refreshing = false,
            loadMoreState = LoadState.Idle,
        )
    )

    override val mutableNewStatusNotifyFlow = MutableSharedFlow<Unit>()
    override val mutableErrorMessageFlow = interactiveHandler.mutableErrorMessageFlow
    override val errorMessageFlow = interactiveHandler.errorMessageFlow
    override val mutableOpenScreenFlow = interactiveHandler.mutableOpenScreenFlow

    override val composedStatusInteraction: ComposedStatusInteraction
        get() = interactiveHandler.composedStatusInteraction

    private var initFeedsJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null
    private var autoFetchNewerFeedsJob: Job? = null

    override fun initController(
        coroutineScope: CoroutineScope,
        roleResolver: IdentityRoleResolver,
        loadFirstPageLocalFeeds: suspend () -> Result<List<Status>>,
        loadNewFromServerFunction: suspend () -> Result<RefreshResult>,
        loadMoreFunction: suspend (maxId: String) -> Result<List<Status>>,
        onStatusUpdate: suspend (Status) -> Unit
    ) {
        this.coroutineScope = coroutineScope
        interactiveHandler.initInteractiveHandler(
            coroutineScope = coroutineScope,
            roleResolver = roleResolver,
            onInteractiveHandleResult = {
                it.handleResult()
            },
        )
        this.loadFirstPageLocalFeeds = loadFirstPageLocalFeeds
        this.loadNewFromServerFunction = loadNewFromServerFunction
        this.loadMoreFunction = loadMoreFunction
        this.onStatusUpdate = onStatusUpdate
    }

    override fun initFeeds(needLocalData: Boolean) {
        initFeedsJob?.cancel()
        initFeedsJob = coroutineScope.launch {
            mutableUiState.update {
                it.copy(
                    showPagingLoadingPlaceholder = true,
                    pageErrorContent = null,
                    feeds = emptyList(),
                )
            }
            if (needLocalData) {
                loadFirstPageLocalFeeds()
                    .onSuccess { localStatus ->
                        if (localStatus.isNotEmpty()) {
                            mutableUiState.update { state ->
                                state.copy(
                                    feeds = localStatus.map { status ->
                                        buildStatusUiState(status)
                                    },
                                    showPagingLoadingPlaceholder = false,
                                )
                            }
                        }
                    }
            }
            loadNewFromServerFunction()
                .onFailure {
                    mutableUiState.update { state ->
                        state.copy(
                            showPagingLoadingPlaceholder = false,
                            pageErrorContent = if (state.feeds.isEmpty()) {
                                it.toTextStringOrNull()
                            } else {
                                null
                            },
                        )
                    }
                    if (mutableUiState.value.feeds.isNotEmpty()) {
                        mutableErrorMessageFlow.emitTextMessageFromThrowable(it)
                    }
                }.onSuccess {
                    mutableUiState.update { state ->
                        state.copy(
                            feeds = it.newStatus.map { buildStatusUiState(it) },
                            showPagingLoadingPlaceholder = false,
                        )
                    }
                }
        }
    }

    override fun startAutoFetchNewerFeeds() {
        if (autoFetchNewerFeedsJob != null) return
        autoFetchNewerFeedsJob = coroutineScope.launch {
            while (true) {
                delay(StatusConfigurationDefault.config.autoFetchNewerFeedsInterval)
                autoFetchNewerFeeds()
            }
        }
    }

    private suspend fun autoFetchNewerFeeds() {
        Log.d("U_TEST", "Controller: start autoFetchNewerFeeds")
        loadNewFromServerFunction()
            .onSuccess {
                Log.d(
                    "U_TEST",
                    "Controller: autoFetchNewerFeeds success, newStatus: ${it.newStatus.size}, delete: ${it.deletedStatus.size}"
                )
                val oldFirstId = mutableUiState.value.feeds.firstOrNull()?.status?.id
                val newFirstId = it.newStatus.firstOrNull()?.id
                mutableUiState.update { state ->
                    state.copy(
                        feeds = state.feeds.applyRefreshResult(it),
                    )
                }

                if (it.newStatus.isNotEmpty() && oldFirstId != newFirstId) {
                    mutableNewStatusNotifyFlow.emit(Unit)
                }
            }
    }

    override fun initInteractiveHandler(
        coroutineScope: CoroutineScope,
        roleResolver: IdentityRoleResolver,
        onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit
    ) {
        interactiveHandler.initInteractiveHandler(
            coroutineScope = coroutineScope,
            roleResolver = roleResolver,
            onInteractiveHandleResult = onInteractiveHandleResult,
        )
    }

    override fun onStatusInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        interactiveHandler.onStatusInteractive(status, uiInteraction)
    }

    override fun onUserInfoClick(blogAuthor: BlogAuthor) {
        interactiveHandler.onUserInfoClick(blogAuthor)
    }

    override fun onStatusClick(status: Status) {
        interactiveHandler.onStatusClick(status)
    }

    override fun onVoted(status: Status, votedOption: List<BlogPoll.Option>) {
        interactiveHandler.onVoted(status, votedOption)
    }

    override fun onFollowClick(target: BlogAuthor) {
        interactiveHandler.onFollowClick(target)
    }

    override fun onUnfollowClick(target: BlogAuthor) {
        interactiveHandler.onUnfollowClick(target)
    }

    override fun onMentionClick(author: BlogAuthor, mention: Mention) {
        interactiveHandler.onMentionClick(author, mention)
    }

    override fun onHashtagClick(status: Status, tag: HashtagInStatus) {
        interactiveHandler.onHashtagClick(status, tag)
    }

    override fun onHashtagClick(author: BlogAuthor, tag: HashtagInStatus) {
        interactiveHandler.onHashtagClick(author, tag)
    }

    override fun onHashtagClick(tag: Hashtag) {
        interactiveHandler.onHashtagClick(tag)
    }

    override fun onRefresh() {
        val uiState = mutableUiState.value
        if (uiState.showPagingLoadingPlaceholder || uiState.refreshing || uiState.loadMoreState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        refreshJob?.cancel()
        refreshJob = coroutineScope.launch {
            mutableUiState.update { it.copy(refreshing = true) }
            loadNewFromServerFunction()
                .onSuccess { refreshResult ->
                    mutableUiState.update {
                        it.copy(
                            refreshing = false,
                            feeds = it.feeds.applyRefreshResult(refreshResult),
                        )
                    }

                }.onFailure { e ->
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(e)
                    mutableUiState.update {
                        it.copy(refreshing = false)
                    }
                }
        }
    }

    override fun onLoadMore() {
        val uiState = mutableUiState.value
        if (uiState.showPagingLoadingPlaceholder || uiState.refreshing || uiState.loadMoreState.loading) return
        val feeds = uiState.feeds
        if (feeds.isEmpty()) return
        loadMoreJob?.cancel()
        loadMoreJob = coroutineScope.launch {
            mutableUiState.update { it.copy(loadMoreState = LoadState.Loading) }
            loadMoreFunction(feeds.last().status.id)
                .onFailure { e ->
                    mutableUiState.update {
                        it.copy(
                            loadMoreState = LoadState.Failed(e.toTextStringOrNull()),
                        )
                    }
                }.onSuccess { list ->
                    mutableUiState.update {
                        it.copy(
                            loadMoreState = LoadState.Idle,
                            feeds = it.feeds.toMutableList(),
                        )
                    }
                }
        }
    }

    private fun List<StatusUiState>.applyRefreshResult(
        refreshResult: RefreshResult,
    ): List<StatusUiState> {
        val deletedIdsSet = refreshResult.deletedStatus
            .map { it.id }
            .toSet()
        val finalList = this.filter {
            !deletedIdsSet.contains(it.status.id)
        }.toMutableList()
        val items = refreshResult.newStatus.map { statusItem ->
            buildStatusUiState(statusItem)
        }
        finalList.addAllIgnoreDuplicate(items)
        return finalList.sortedByDescending { it.status.datetime }
    }

    private fun MutableList<StatusUiState>.addAllIgnoreDuplicate(
        newItems: List<StatusUiState>,
    ) {
        newItems.forEach { this.addIfNotExist(it) }
    }

    private fun MutableList<StatusUiState>.addIfNotExist(newItemUiState: StatusUiState) {
        if (this.container { it.status.id == newItemUiState.status.id }) return
        this += newItemUiState
    }

    private suspend fun InteractiveHandleResult.handleResult() {
        this.handle(
            uiStatusUpdater = { newUiState ->
                onStatusUpdate(newUiState.status)
                mutableUiState.update { currentUiState ->
                    val newFeeds = currentUiState.feeds.map {
                        if (it.status.id == newUiState.status.id) {
                            it.copy(status = newUiState.status)
                        } else {
                            it
                        }
                    }
                    currentUiState.copy(feeds = newFeeds)
                }
            },
            followStateUpdater = { _, _ ->

            }
        )
    }


}

data class CommonFeedsUiState(
    val feeds: List<StatusUiState>,
    val showPagingLoadingPlaceholder: Boolean,
    val pageErrorContent: TextString?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
)
