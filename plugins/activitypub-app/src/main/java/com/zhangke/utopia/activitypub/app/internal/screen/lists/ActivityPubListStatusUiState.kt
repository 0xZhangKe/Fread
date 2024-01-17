package com.zhangke.utopia.activitypub.app.internal.screen.lists

import com.zhangke.framework.composable.TextString
import com.zhangke.utopia.common.status.model.StatusUiState

data class ActivityPubListStatusUiState(
    val status: List<StatusUiState>,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val errorMessage: TextString?,
)

sealed interface LoadState {

    data object Idle : LoadState

    data object Loading : LoadState

    data class Failed(val e: Throwable) : LoadState
}
