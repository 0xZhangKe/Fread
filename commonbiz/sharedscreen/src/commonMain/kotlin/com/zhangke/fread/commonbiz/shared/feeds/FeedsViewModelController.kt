package com.zhangke.fread.commonbiz.shared.feeds

import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.blog.Blog
import com.zhangke.fread.status.blog.BlogPoll
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.HashtagInStatus
import com.zhangke.fread.status.model.Mention
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusActionType
import com.zhangke.fread.status.model.StatusProviderProtocol
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.updateStatus
import com.zhangke.fread.status.richtext.preParse
import com.zhangke.fread.status.status.model.Status
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedsViewModelController(
    statusProvider: StatusProvider,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    statusUpdater: StatusUpdater,
    refactorToNewStatus: RefactorToNewStatusUseCase,
) : IFeedsViewModelController {

    private lateinit var coroutineScope: CoroutineScope
    private lateinit var locatorResolver: (Status) -> PlatformLocator
    private lateinit var loadFirstPageLocalFeeds: suspend () -> Result<List<StatusUiState>>
    private lateinit var loadNewFromServerFunction: suspend () -> Result<RefreshResult>
    private lateinit var loadMoreFunction: suspend (maxId: String) -> Result<List<StatusUiState>>
    private lateinit var onStatusUpdate: suspend (Status) -> Unit

    private val interactiveHandler = InteractiveHandler(
        statusProvider = statusProvider,
        statusUpdater = statusUpdater,
        statusUiStateAdapter = statusUiStateAdapter,
        refactorToNewStatus = refactorToNewStatus,
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
        locatorResolver: (Status) -> PlatformLocator,
        loadFirstPageLocalFeeds: suspend () -> Result<List<StatusUiState>>,
        loadNewFromServerFunction: suspend () -> Result<RefreshResult>,
        loadMoreFunction: suspend (maxId: String) -> Result<List<StatusUiState>>,
        onStatusUpdate: suspend (Status) -> Unit
    ) {
        this.coroutineScope = coroutineScope
        this.locatorResolver = locatorResolver
        this.loadFirstPageLocalFeeds = loadFirstPageLocalFeeds
        this.loadNewFromServerFunction = loadNewFromServerFunction
        this.loadMoreFunction = loadMoreFunction
        this.onStatusUpdate = onStatusUpdate
        interactiveHandler.initInteractiveHandler(
            coroutineScope = coroutineScope,
            onInteractiveHandleResult = {
                it.handleResult()
            },
        )
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
                    .map { list ->
                        list.preParse()
                        list
                    }
                    .onSuccess { localStatus ->
                        if (localStatus.isNotEmpty()) {
                            mutableUiState.update { state ->
                                state.copy(
                                    feeds = localStatus,
                                    showPagingLoadingPlaceholder = false,
                                )
                            }
                        }
                    }
            }
            loadNewFromServerFunction()
                .map { result ->
                    val newStatus = result.newStatus
                    newStatus.preParse()
                    newStatus
                }
                .onFailure {
                    mutableUiState.update { state ->
                        state.copy(
                            showPagingLoadingPlaceholder = false,
                            pageErrorContent = if (state.feeds.isEmpty()) {
                                it
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
                            feeds = it,
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
        loadNewFromServerFunction()
            .map {
                it.newStatus.preParse()
                it
            }
            .onSuccess {
                val oldFirstId = mutableUiState.value.feeds.firstOrNull()?.status?.id
                val newFirstId = it.newStatus.firstOrNull()?.status?.id
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
        onInteractiveHandleResult: suspend (InteractiveHandleResult) -> Unit
    ) {
        interactiveHandler.initInteractiveHandler(
            coroutineScope = coroutineScope,
            onInteractiveHandleResult = onInteractiveHandleResult,
        )
    }

    override fun onStatusInteractive(
        status: StatusUiState,
        type: StatusActionType
    ) {
        interactiveHandler.onStatusInteractive(status, type)
    }

    override fun onUserInfoClick(locator: PlatformLocator, blogAuthor: BlogAuthor) {
        interactiveHandler.onUserInfoClick(locator, blogAuthor)
    }

    override fun onStatusClick(status: StatusUiState) {
        interactiveHandler.onStatusClick(status)
    }

    override fun onBlogClick(locator: PlatformLocator, blog: Blog) {
        interactiveHandler.onBlogClick(locator, blog)
    }

    override fun onVoted(status: StatusUiState, votedOption: List<BlogPoll.Option>) {
        interactiveHandler.onVoted(status, votedOption)
    }

    override fun onFollowClick(locator: PlatformLocator, target: BlogAuthor) {
        interactiveHandler.onFollowClick(locator, target)
    }

    override fun onUnfollowClick(locator: PlatformLocator, target: BlogAuthor) {
        interactiveHandler.onUnfollowClick(locator, target)
    }

    override fun onMentionClick(locator: PlatformLocator, mention: Mention) {
        interactiveHandler.onMentionClick(locator, mention)
    }

    override fun onMentionClick(
        locator: PlatformLocator,
        did: String,
        protocol: StatusProviderProtocol
    ) {
        interactiveHandler.onMentionClick(locator, did, protocol)
    }

    override fun onHashtagClick(locator: PlatformLocator, tag: HashtagInStatus) {
        interactiveHandler.onHashtagClick(locator, tag)
    }

    override fun onHashtagClick(locator: PlatformLocator, tag: Hashtag) {
        interactiveHandler.onHashtagClick(locator, tag)
    }

    override fun onMaybeHashtagClick(
        locator: PlatformLocator,
        protocol: StatusProviderProtocol,
        tag: String,
    ) {
        interactiveHandler.onMaybeHashtagClick(locator, protocol, tag)
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
                        val newList = it.feeds.toMutableList()
                        newList.addAllIgnoreDuplicate(list)
                        it.copy(
                            loadMoreState = LoadState.Idle,
                            feeds = newList,
                        )
                    }
                }
        }
    }

    private fun List<StatusUiState>.applyRefreshResult(
        refreshResult: RefreshResult,
    ): List<StatusUiState> {
        if (refreshResult.useOldData) {
            val deletedIdsSet = refreshResult.deletedStatus
                .map { it.status.id }
                .toSet()
            val oldList = this.filter { !deletedIdsSet.contains(it.status.id) }
            val addedNewList = refreshResult.newStatus
                .toMutableList()
            addedNewList.addAllIgnoreDuplicate(oldList)
            return addedNewList
        } else {
            return refreshResult.newStatus
        }
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
                    currentUiState.copy(feeds = currentUiState.feeds.updateStatus(newUiState))
                }
            },
            deleteStatus = { deletedStatusId ->
                mutableUiState.update { currentUiState ->
                    currentUiState.copy(
                        feeds = currentUiState.feeds.filter { it.status.id != deletedStatusId }
                    )
                }
            },
            followStateUpdater = { _, _ ->

            }
        )
    }
}
