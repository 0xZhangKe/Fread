package com.zhangke.fread.bluesky.internal.screen.feeds.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.actor.PreferencesUnion.SavedFeedsPrefV2
import app.bsky.feed.GetSuggestedFeedsQueryParams
import com.zhangke.framework.collections.updateItem
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.framework.utils.exceptionOrThrow
import com.zhangke.fread.bluesky.internal.adapter.BlueskyFeedsAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.PinFeedsUseCase
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
class ExplorerFeedsViewModel(
    private val clientManager: BlueskyClientManager,
    private val feedsAdapter: BlueskyFeedsAdapter,
    private val followFeeds: PinFeedsUseCase,
    private val locator: PlatformLocator,
) : ViewModel() {

    companion object {

        private const val FLAG_CURSOR_ENDING = "flag_cursor_ending_for_suggested_feeds"
    }

    private val _uiState = MutableStateFlow(ExplorerFeedsUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    private var refreshMoreJob: Job? = null
    private var loadMoreJob: Job? = null
    private var cursor: String? = null

    private val pinnedFeedsUris = mutableListOf<String>()

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
        val client = clientManager.getClient(locator)
        return supervisorScope {
            val pinnedFeedsDeferred = async { getPinnedFeeds() }
            val feedsListDeferred = async {
                client.getSuggestedFeedsCatching(GetSuggestedFeedsQueryParams(cursor = cursor))
                    .onSuccess {
                        this@ExplorerFeedsViewModel.cursor =
                            if (it.cursor.isNullOrBlank()) FLAG_CURSOR_ENDING else it.cursor
                    }
            }
            val pinnedFeeds = pinnedFeedsDeferred.await().getOrNull() ?: emptyList()
            feedsListDeferred.await().map {
                it.feeds.map { item ->
                    BlueskyFeedsUiState(
                        followRequesting = false,
                        feeds = feedsAdapter.convertToFeeds(
                            generator = item,
                            pinned = pinnedFeeds.any { uri -> uri == item.uri.atUri },
                        ),
                    )
                }
            }
        }
    }

    fun onFollowClick(feedsUiState: BlueskyFeedsUiState) {
        if (feedsUiState.followRequesting) return
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    feeds = state.feeds.updateItem(feedsUiState) {
                        it.copy(followRequesting = true)
                    },
                )
            }
            followFeeds(locator, feedsUiState.feeds)
                .onSuccess {
                    _uiState.update { state ->
                        state.copy(
                            feeds = state.feeds.map { item ->
                                if (item.feeds.uri == feedsUiState.feeds.uri) {
                                    item.copy(
                                        feeds = item.feeds.copy(pinned = true),
                                        followRequesting = false,
                                    )
                                } else {
                                    item
                                }
                            },
                        )
                    }
                    pinnedFeedsUris += feedsUiState.feeds.uri
                }.onFailure { t ->
                    _uiState.update { state ->
                        state.copy(
                            feeds = state.feeds.map { item ->
                                if (item.feeds.uri == feedsUiState.feeds.uri) {
                                    item.copy(followRequesting = false)
                                } else {
                                    item
                                }
                            },
                        )
                    }
                    _snackBarMessage.emitTextMessageFromThrowable(t)
                }
        }
    }

    fun onFeedsUpdate(feeds: BlueskyFeeds.Feeds) {
        _uiState.update { state ->
            state.copy(
                feeds = state.feeds.map {
                    if (it.feeds.cid == feeds.cid) {
                        it.copy(feeds = feeds)
                    } else {
                        it
                    }
                }
            )
        }
        if (feeds.pinned) {
            pinnedFeedsUris += feeds.uri
        } else {
            pinnedFeedsUris -= feeds.uri
        }
    }

    private suspend fun getPinnedFeeds(): Result<List<String>> {
        if (pinnedFeedsUris.isNotEmpty()) return Result.success(pinnedFeedsUris)
        val preferenceResult = clientManager.getClient(locator).getPreferencesCatching()
        if (preferenceResult.isFailure) return Result.failure(preferenceResult.exceptionOrThrow())
        return preferenceResult.map {
            it.preferences.filterIsInstance<SavedFeedsPrefV2>()
                .firstOrNull()
                ?.value
                ?.items
                ?: emptyList()
        }.map { feeds -> feeds.map { it.value } }
            .onSuccess { pinnedFeedsUris += it }
    }
}
