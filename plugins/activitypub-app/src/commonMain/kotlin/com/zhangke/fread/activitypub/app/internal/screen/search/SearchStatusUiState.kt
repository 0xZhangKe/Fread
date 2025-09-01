package com.zhangke.fread.activitypub.app.internal.screen.search

import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.status.model.StatusUiState

data class SearchStatusUiState(
    val query: String,
    val searching: Boolean,
    val loadMoreState: LoadState,
    val result: List<StatusUiState>,
) {

    companion object {

        fun default() = SearchStatusUiState(
            query = "",
            searching = false,
            loadMoreState = LoadState.Idle,
            result = emptyList(),
        )
    }
}
