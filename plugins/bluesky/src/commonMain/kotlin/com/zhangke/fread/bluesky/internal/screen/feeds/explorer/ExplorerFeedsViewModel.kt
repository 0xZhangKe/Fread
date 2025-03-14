package com.zhangke.fread.bluesky.internal.screen.feeds.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.feed.GetSuggestedFeedsQueryParams
import com.zhangke.framework.collections.updateItem
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.bluesky.internal.adapter.BlueskyFeedsAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.FollowFeedsUseCase
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

class ExplorerFeedsViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val feedsAdapter: BlueskyFeedsAdapter,
    private val followFeeds: FollowFeedsUseCase,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    companion object {

        private const val FLAG_CURSOR_ENDING = "flag_cursor_ending_for_suggested_feeds"
    }

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
        ): ExplorerFeedsViewModel
    }

    private val _uiState = MutableStateFlow(ExplorerFeedsUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    private var refreshMoreJob: Job? = null
    private var loadMoreJob: Job? = null
    private var cursor: String? = null

    init {
        launchInViewModel {
            _uiState.update { it.copy(initializing = true) }
            getSuggestedFeeds(null)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            initializing = false,
                            feeds = list,
                        )
                    }
                }.onFailure { t ->
                    _uiState.update { it.copy(initializing = false, pageError = t) }
                }
        }
    }

    fun onRefresh() {
        if (refreshMoreJob?.isActive == true) return
        refreshMoreJob?.cancel()
        refreshMoreJob = viewModelScope.launch {
            _uiState.update { it.copy(refreshing = true) }
            getSuggestedFeeds(null)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            refreshing = false,
                            feeds = list,
                        )
                    }
                }.onFailure { t ->
                    _uiState.update { it.copy(refreshing = false) }
                    _snackBarMessage.emitTextMessageFromThrowable(t)
                }
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
                            feeds = it.feeds + list,
                        )
                    }
                }.onFailure { t ->
                    _uiState.update {
                        it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull()))
                    }
                }
        }
    }

    private suspend fun getSuggestedFeeds(cursor: String? = this.cursor): Result<List<BlueskyFeedsUiState>> {
        return clientManager.getClient(role)
            .getSuggestedFeedsCatching(GetSuggestedFeedsQueryParams(cursor = cursor))
            .onSuccess {
                this.cursor = if (it.cursor.isNullOrBlank()) FLAG_CURSOR_ENDING else it.cursor
            }
            .map {
                it.feeds.map { item ->
                    BlueskyFeedsUiState(
                        followRequesting = false,
                        feeds = feedsAdapter.convertToFeeds(
                            generator = item,
                            following = false,
                            pinned = false
                        ),
                    )
                }
            }
    }

    fun onFollowClick(feedsUiState: BlueskyFeedsUiState) {
        if (feedsUiState.followRequesting) return
        if (feedsUiState.feeds !is BlueskyFeeds.Feeds) return
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    feeds = state.feeds.updateItem(feedsUiState) {
                        it.copy(followRequesting = true)
                    },
                )
            }
            followFeeds(role, feedsUiState.feeds)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(feeds = state.feeds.filter { it.feeds != feedsUiState.feeds })
                    }
                }.onFailure { t ->
                    _uiState.update { state ->
                        state.copy(
                            feeds = state.feeds.updateItem(feedsUiState) {
                                it.copy(followRequesting = false)
                            },
                        )
                    }
                    _snackBarMessage.emitTextMessageFromThrowable(t)
                }
        }
    }
}
