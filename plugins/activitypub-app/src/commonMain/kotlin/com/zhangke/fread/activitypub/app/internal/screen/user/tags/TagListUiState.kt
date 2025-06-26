package com.zhangke.fread.activitypub.app.internal.screen.user.tags

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator

data class TagListUiState(
    val locator: PlatformLocator,
    val refreshing: Boolean,
    val tags: List<Hashtag>,
    val loadState: LoadState,
) {

    companion object {

        fun default(locator: PlatformLocator) = TagListUiState(
            locator = locator,
            refreshing = false,
            tags = emptyList(),
            loadState = LoadState.Idle,
        )
    }
}
