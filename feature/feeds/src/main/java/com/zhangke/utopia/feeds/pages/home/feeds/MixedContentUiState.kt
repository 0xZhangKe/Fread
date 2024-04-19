package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.model.IdentityRole

data class MixedContentUiState(
    val feeds: List<MixedContentItemUiState>,
    val showPagingLoadingPlaceholder: Boolean,
    val pageErrorContent: TextString?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
) {

    companion object {

        val initialUiState = MixedContentUiState(
            feeds = emptyList(),
            showPagingLoadingPlaceholder = false,
            pageErrorContent = null,
            refreshing = false,
            loadMoreState = LoadState.Idle,
        )
    }
}

data class MixedContentItemUiState(
    val role: IdentityRole,
    val statusUiState: StatusUiState,
)
