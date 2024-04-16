package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.model.IdentityRole

data class MixedContentUiState(
    val feeds: List<MixedContentItemUiState>,
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

data class MixedContentItemUiState(
    val role: IdentityRole,
    val statusUiState: StatusUiState,
)
