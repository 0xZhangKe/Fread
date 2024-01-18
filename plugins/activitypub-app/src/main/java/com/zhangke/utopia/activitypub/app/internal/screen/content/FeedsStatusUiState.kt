package com.zhangke.utopia.activitypub.app.internal.screen.content

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.common.status.model.StatusUiState

data class FeedsStatusUiState(
    val status: List<StatusUiState>,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
    val errorMessage: TextString?,
)
