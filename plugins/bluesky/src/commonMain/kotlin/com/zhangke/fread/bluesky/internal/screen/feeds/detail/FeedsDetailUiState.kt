package com.zhangke.fread.bluesky.internal.screen.feeds.detail

import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds

data class FeedsDetailUiState(
    val feeds: BlueskyFeeds.Feeds,
) {

    companion object {

        fun default(feeds: BlueskyFeeds.Feeds): FeedsDetailUiState {
            return FeedsDetailUiState(
                feeds = feeds,
            )
        }
    }
}
