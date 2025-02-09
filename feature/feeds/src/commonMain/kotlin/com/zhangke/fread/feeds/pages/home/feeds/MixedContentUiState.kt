package com.zhangke.fread.feeds.pages.home.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.model.StatusUiState

data class MixedContentUiState(
    val content: MixedContent?,
    val dataList: List<StatusUiState>,
    val initializing: Boolean,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val pageError: TextString?,
) {

    companion object {

        fun default(): MixedContentUiState {
            return MixedContentUiState(
                content = null,
                dataList = emptyList(),
                initializing = true,
                refreshing = false,
                loadMoreState = LoadState.Idle,
                pageError = null
            )
        }
    }
}
