package com.zhangke.fread.bluesky.internal.screen.feeds.following

import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.status.model.PlatformLocator

data class BskyFeedsExplorerUiState(
    val initializing: Boolean,
    val locator: PlatformLocator?,
    val pageError: Throwable?,
    val refreshing: Boolean,
    val followingFeeds: List<BlueskyFeeds>,
    val reordering: Boolean,
) {

    companion object {

        fun default(): BskyFeedsExplorerUiState {
            return BskyFeedsExplorerUiState(
                initializing = false,
                refreshing = false,
                locator = null,
                pageError = null,
                followingFeeds = emptyList(),
                reordering = false,
            )
        }
    }
}
