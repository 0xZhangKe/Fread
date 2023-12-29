package com.zhangke.utopia.commonbiz.shared.screen.status.context

import com.zhangke.utopia.common.status.model.StatusUiState

data class StatusContextUiState(
    val contextStatus: List<StatusInContext>,
){

    val anchorIndex: Int get() = contextStatus.indexOfFirst { it.type == StatusInContextType.ANCHOR }
}

data class StatusInContext(
    val status: StatusUiState,
    val type: StatusInContextType,
)

enum class StatusInContextType {

    ANCHOR,
    ANCESTOR,
    DESCENDANT,
}
