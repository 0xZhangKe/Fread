package com.zhangke.fread.bluesky.internal.screen.feeds.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.feed.GetSuggestedFeedsQueryParams
import com.zhangke.framework.collections.updateItem
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.bluesky.internal.adapter.BlueskyFeedsAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.FollowFeedsUseCase
import com.zhangke.fread.bluesky.internal.usecase.GetFollowingFeedsUseCase
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class BskyFeedsExplorerViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val getFollowingFeeds: GetFollowingFeedsUseCase,
    private val followFeeds: FollowFeedsUseCase,
    private val feedsAdapter: BlueskyFeedsAdapter,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    companion object {

        private const val FLAG_CURSOR_ENDING = "flag_cursor_ending_for_suggested_feeds"
    }

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
        ): BskyFeedsExplorerViewModel
    }

    private val _uiState = MutableStateFlow(BskyFeedsExplorerUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    private var cursor: String? = null

    private var initJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        loadFeedsList(false)
    }

    fun onRefresh() {
        loadFeedsList(true)
    }

    private fun loadFeedsList(refreshing: Boolean) {
        if (initJob?.isActive == true) return
        initJob?.cancel()
        cursor = null
        initJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    initializing = !refreshing,
                    refreshing = refreshing,
                    pageError = null,
                )
            }
            getFollowingFeeds(role)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            initializing = false,
                            refreshing = false,
                            followingFeeds = list,
                        )
                    }
                }.onFailure { t ->
                    _uiState.update {
                        it.copy(
                            initializing = false,
                            refreshing = false,
                            pageError = t,
                        )
                    }
                }
            onLoadMore()
        }
    }

    fun onLoadMore() {
        if (cursor == FLAG_CURSOR_ENDING) return
        if (loadMoreJob?.isActive == true) return
        loadMoreJob?.cancel()
        loadMoreJob = viewModelScope.launch {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            getSuggestedFeeds()
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            loadMoreState = LoadState.Idle,
                            suggestedFeeds = it.suggestedFeeds + list,
                        )
                    }
                }.onFailure { t ->
                    _uiState.update {
                        it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull()))
                    }
                }
        }
    }

    private suspend fun getSuggestedFeeds(): Result<List<BlueskyFeedsUiState>> {
        return clientManager.getClient(role)
            .getSuggestedFeedsCatching(GetSuggestedFeedsQueryParams(cursor = cursor))
            .onSuccess {
                this.cursor = if (it.cursor.isNullOrBlank()) FLAG_CURSOR_ENDING else it.cursor
            }
            .map {
                it.feeds.map { item ->
                    BlueskyFeedsUiState(false, feedsAdapter.convertToFeeds(item, false, false))
                }
            }
    }

    fun onAddFeedsClick(feeds: BlueskyFeedsUiState) {
        if (feeds.loading) return
        if (feeds.feeds !is BlueskyFeeds.Feeds) return
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(suggestedFeeds = state.suggestedFeeds.updateItem(feeds) {
                    it.copy(loading = true)
                })
            }
            followFeeds(role, feeds.feeds)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(suggestedFeeds = state.suggestedFeeds.filter { it.feeds != feeds.feeds })
                    }
                    getFollowingFeeds(role)
                        .onSuccess { followingFeeds ->
                            _uiState.update { it.copy(followingFeeds = followingFeeds) }
                        }
                }.onFailure {
                    _uiState.update { state ->
                        state.copy(suggestedFeeds = state.suggestedFeeds.updateItem(feeds) {
                            it.copy(loading = false)
                        })
                    }
                    _snackBarMessage.emitTextMessageFromThrowable(it)
                }
        }
    }
}
