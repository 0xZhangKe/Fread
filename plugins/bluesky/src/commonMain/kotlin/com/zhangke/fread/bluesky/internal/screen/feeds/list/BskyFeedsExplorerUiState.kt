package com.zhangke.fread.bluesky.internal.screen.feeds.list

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds

data class BskyFeedsExplorerUiState(
    val initializing: Boolean,
    val pageError: Throwable?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val followingFeeds: List<BlueskyFeeds>,
    val suggestedFeeds: List<BlueskyFeedsUiState>,
) {

    companion object {

        fun default(): BskyFeedsExplorerUiState {
            return BskyFeedsExplorerUiState(
                initializing = false,
                loadMoreState = LoadState.Idle,
                refreshing = false,
                pageError = null,
                followingFeeds = emptyList(),
                suggestedFeeds = emptyList(),
            )
        }
    }
}

data class BlueskyFeedsUiState(
    val loading: Boolean,
    val feeds: BlueskyFeeds,
)
