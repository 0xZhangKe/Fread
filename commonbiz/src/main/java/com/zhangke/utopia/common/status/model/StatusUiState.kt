package com.zhangke.utopia.common.status.model

import com.zhangke.utopia.status.status.model.Status

data class StatusUiState(
    val status: Status,
    val displayTime: String,
    val bottomInteractions: List<StatusUiInteraction>,
    val moreInteractions: List<StatusUiInteraction>,
)

inline fun List<StatusUiState>.updateById(
    statusId: String,
    block: (StatusUiState) -> StatusUiState,
): List<StatusUiState> {
    return map {
        if (it.status.id == statusId) {
            block(it)
        } else {
            it
        }
    }
}
