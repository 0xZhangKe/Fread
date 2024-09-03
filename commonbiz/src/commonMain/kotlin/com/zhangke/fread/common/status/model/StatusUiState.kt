package com.zhangke.fread.common.status.model

import com.zhangke.framework.utils.PlatformSerializable
import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import kotlinx.serialization.Serializable

@Serializable
data class StatusUiState(
    val status: Status,
    val blogTranslationState: BlogTranslationUiState,
    val role: IdentityRole,
    val displayTime: String,
    val specificTime: String,
    val editedTime: String?,
    val following: Boolean? = null,
    val bottomInteractions: List<StatusUiInteraction>,
    val moreInteractions: List<StatusUiInteraction>,
) : PlatformSerializable

@Serializable
data class BlogTranslationUiState(
    val support: Boolean,
    val translating: Boolean = false,
    val showingTranslation: Boolean = false,
    val blogTranslation: BlogTranslation? = null,
) : PlatformSerializable

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
