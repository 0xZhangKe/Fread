package com.zhangke.fread.bluesky.internal.screen.feeds.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bsky.feed.GetSuggestedFeedsQueryParams
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.bluesky.internal.adapter.BlueskyFeedsAdapter
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.GetFollowingFeedsUseCase
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class BskyFeedsExplorerViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val getFollowingFeeds: GetFollowingFeedsUseCase,
    private val feedsAdapter: BlueskyFeedsAdapter,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
        ): BskyFeedsExplorerViewModel
    }

    private val _uiState = MutableStateFlow(BskyFeedsExplorerUiState.default())
    val uiState = _uiState.asStateFlow()

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
                .map { list -> list.map { feedsAdapter.convertToFeeds(it) } }
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
            getSuggestedFeeds().onSuccess { list ->
                _uiState.update { it.copy(suggestedFeeds = list) }
            }
        }
    }

    fun onLoadMore() {
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

    private suspend fun getSuggestedFeeds(): Result<List<BlueskyFeeds>> {
        return clientManager.getClient(role)
            .getSuggestedFeedsCatching(GetSuggestedFeedsQueryParams(cursor = cursor))
            .onSuccess { this.cursor = it.cursor }
            .map { it.feeds.map { item -> feedsAdapter.convertToFeeds(item) } }
    }
}
