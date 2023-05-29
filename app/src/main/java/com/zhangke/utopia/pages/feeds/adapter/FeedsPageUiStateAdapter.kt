package com.zhangke.utopia.pages.feeds.adapter

import com.zhangke.framework.composable.LoadableState
import com.zhangke.utopia.domain.Feeds
import com.zhangke.utopia.pages.feeds.FeedsPageUiState
import javax.inject.Inject

class FeedsPageUiStateAdapter @Inject constructor() {

    fun adapt(
        feeds: Feeds,
    ): FeedsPageUiState {
        return FeedsPageUiState(
            name = feeds.name,
            sourceList = feeds.sourceList,
            feeds = LoadableState.idle(),
        )
    }
}
