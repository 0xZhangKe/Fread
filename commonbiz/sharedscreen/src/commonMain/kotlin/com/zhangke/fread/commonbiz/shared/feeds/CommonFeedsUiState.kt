package com.zhangke.fread.commonbiz.shared.feeds

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.common.status.model.StatusUiState

data class CommonFeedsUiState(
    val feeds: List<StatusUiState>,
    val showPagingLoadingPlaceholder: Boolean,
    val pageErrorContent: TextString?,
    val refreshing: Boolean,
    val loadMoreState: LoadState,
)
