package com.zhangke.fread.bluesky.internal.screen.feeds.explorer

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds

data class ExplorerFeedsUiState(
    val initializing: Boolean,
    val pageError: Throwable?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val feeds: List<BlueskyFeedsUiState>,
) {

    companion object {

        fun default(): ExplorerFeedsUiState {
            return ExplorerFeedsUiState(
                initializing = false,
                pageError = null,
                refreshing = false,
                loadMoreState = LoadState.Idle,
                feeds = emptyList(),
            )
        }
    }
}

data class BlueskyFeedsUiState(
    val followRequesting: Boolean,
    val feeds: BlueskyFeeds.Feeds,
)
