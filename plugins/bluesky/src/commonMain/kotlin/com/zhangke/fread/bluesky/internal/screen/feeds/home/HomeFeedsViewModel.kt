package com.zhangke.fread.bluesky.internal.screen.feeds.home

import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.GetFeedsStatusUseCase
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.richtext.preParseStatusUiState

class HomeFeedsViewModel(
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val statusProvider: StatusProvider,
    private val getFeedsStatus: GetFeedsStatusUseCase,
    refactorToNewStatus: RefactorToNewStatusUseCase,
    statusUpdater: StatusUpdater,
    private val feeds: BlueskyFeeds,
    private val locator: PlatformLocator,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    companion object {

        private const val FLAG_CURSOR_ENDING = "flag_cursor_ending_for_suggested_feeds"
    }

    private var cursor: String? = null

    init {
        initController(
            coroutineScope = viewModelScope,
            locatorResolver = { locator },
            loadFirstPageLocalFeeds = {
                Result.success(emptyList())
            },
            loadNewFromServerFunction = ::loadNewDataFromServer,
            loadMoreFunction = { loadMoreDataFromServer() },
            onStatusUpdate = {},
        )
        initFeeds(false)
    }

    private suspend fun loadNewDataFromServer(): Result<RefreshResult> {
        return loadFeeds(null).map {
            RefreshResult(
                newStatus = it,
                deletedStatus = emptyList(),
            )
        }
    }

    private suspend fun loadMoreDataFromServer(): Result<List<StatusUiState>> {
        if (cursor == FLAG_CURSOR_ENDING) return Result.success(emptyList())
        return loadFeeds()
    }

    private suspend fun loadFeeds(cursor: String? = this.cursor): Result<List<StatusUiState>> {
        return getFeedsStatus(locator = locator, feeds = feeds, cursor = cursor).map {
            this.cursor = if (it.cursor.isNullOrBlank()) FLAG_CURSOR_ENDING else it.cursor
            it.feeds.onEach { status -> status.preParseStatusUiState() }
        }
    }

    fun onPageResume() {
        if (uiState.value.pageErrorContent == null) return
        initFeeds(true)
    }
}
