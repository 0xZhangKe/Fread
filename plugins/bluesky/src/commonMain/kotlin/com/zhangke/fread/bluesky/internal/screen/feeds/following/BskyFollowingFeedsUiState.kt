package com.zhangke.fread.bluesky.internal.screen.feeds.following

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds

data class BskyFeedsExplorerUiState(
    val initializing: Boolean,
    val pageError: Throwable?,
    val refreshing: Boolean,
    val followingFeeds: List<BlueskyFeeds>,
) {

    companion object {

        fun default(): BskyFeedsExplorerUiState {
            return BskyFeedsExplorerUiState(
                initializing = false,
                refreshing = false,
                pageError = null,
                followingFeeds = emptyList(),
            )
        }
    }
}
