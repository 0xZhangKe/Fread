package com.zhangke.fread.common.status.model

import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import kotlinx.serialization.Serializable

@Serializable
data class StatusUiState(
    val status: Status,
    val role: IdentityRole,
    val displayTime: String,
    val specificTime : String,
    val editedTime: String?,
    val bottomInteractions: List<StatusUiInteraction>,
    val moreInteractions: List<StatusUiInteraction>,
) : java.io.Serializable

fun List<StatusUiState>.updateStatus(
    status: StatusUiState,
): List<StatusUiState> {
    return map {
        if (it.status.intrinsicBlog.id == status.status.intrinsicBlog.id) {
            status
        } else {
            it
        }
    }
}
