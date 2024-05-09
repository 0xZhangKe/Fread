package com.zhangke.utopia.common.status.model

import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status

data class StatusUiState(
    val status: Status,
    val role: IdentityRole,
    val displayTime: String,
    val bottomInteractions: List<StatusUiInteraction>,
    val moreInteractions: List<StatusUiInteraction>,
)

fun List<StatusUiState>.updateStatus(
    status: StatusUiState,
): List<StatusUiState> {
    return map {
        if (it.status.id == status.status.id) {
            status
        } else {
            it
        }
    }
}

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
