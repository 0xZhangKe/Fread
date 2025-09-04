package com.zhangke.fread.commonbiz.shared.screen.status.context

import com.zhangke.framework.composable.TextString
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.StatusUiState

data class StatusContextUiState(
    val contextStatus: List<StatusInContext>,
    val loading: Boolean,
    val needScrollToAnchor: Boolean,
    val currentAccount: LoggedAccount?,
    val errorMessage: TextString?,
) {

    val anchorIndex: Int get() = contextStatus.indexOfFirst { it.type == StatusInContextType.ANCHOR }
}

data class StatusInContext(
    val status: StatusUiState,
    val type: StatusInContextType,
)

enum class StatusInContextType {

    ANCHOR,
    ANCESTOR,
    DESCENDANT,// no descendant
    DESCENDANT_ANCHOR,
    DESCENDANT_WITH_ANCESTOR_DESCENDANT,
    DESCENDANT_WITH_ANCESTOR,
}
