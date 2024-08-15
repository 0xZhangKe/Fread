package com.zhangke.fread.common.status.model

import com.zhangke.fread.status.blog.BlogTranslation
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import kotlinx.serialization.Serializable

@Serializable
data class StatusUiState(
    val status: Status,
    val blogTranslationState: BlogTranslationUiState? = null,
    val role: IdentityRole,
    val displayTime: String,
    val specificTime: String,
    val editedTime: String?,
    val following: Boolean? = null,
    val bottomInteractions: List<StatusUiInteraction>,
    val moreInteractions: List<StatusUiInteraction>,
) : java.io.Serializable

@Serializable
sealed class BlogTranslationUiState : java.io.Serializable {

    @Serializable
    data object Loading : BlogTranslationUiState(), java.io.Serializable {
        private fun readResolve(): Any = Loading
    }

    @Serializable
    data class Success(
        val blogTranslation: BlogTranslation,
    ) : BlogTranslationUiState(), java.io.Serializable
}

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
