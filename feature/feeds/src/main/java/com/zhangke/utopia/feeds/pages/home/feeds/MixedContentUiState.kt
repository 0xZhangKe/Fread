package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.status.model.StatusUiState

data class MixedContentUiState(
    val feeds: List<StatusUiState>,
    /**
     * 初始化中，feeds 为空时UI层显示loading
     */
    val initializing: Boolean,
    val initErrorMessage: TextString?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
) {

    companion object {

        val initialUiState = MixedContentUiState(
            feeds = emptyList(),
            initializing = false,
            initErrorMessage = null,
            refreshing = false,
            loadMoreState = LoadState.Idle,
        )
    }
}
