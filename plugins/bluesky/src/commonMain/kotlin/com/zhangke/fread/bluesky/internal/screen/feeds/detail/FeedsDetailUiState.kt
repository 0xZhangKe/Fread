package com.zhangke.fread.bluesky.internal.screen.feeds.detail

import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds

data class FeedsDetailUiState(
    val feeds: BlueskyFeeds,
) {

    companion object {

        fun default(feeds: BlueskyFeeds): FeedsDetailUiState {
            return FeedsDetailUiState(
                feeds = feeds,
            )
        }
    }
}
