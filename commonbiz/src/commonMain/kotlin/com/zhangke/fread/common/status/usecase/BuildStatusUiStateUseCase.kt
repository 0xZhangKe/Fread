package com.zhangke.fread.common.status.usecase

import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject

class BuildStatusUiStateUseCase @Inject constructor() {

    operator fun invoke(
        statusUiStatus: StatusUiState,
        status: Status,
        following: Boolean? = null,
        blogTranslationState: BlogTranslationUiState? = null,
    ): StatusUiState {
        return StatusUiState(
            status = status,
            role = statusUiStatus.role,
            logged = statusUiStatus.logged,
            isOwner = statusUiStatus.isOwner,
            blogTranslationState = blogTranslationState ?: BlogTranslationUiState(
                support = status.intrinsicBlog.supportTranslate,
                translating = false,
                showingTranslation = false,
                blogTranslation = null,
            ),
            following = following ?: statusUiStatus.following,
        )
    }

    operator fun invoke(
        role: IdentityRole,
        status: Status,
        following: Boolean? = null,
        logged: Boolean = false,
        isOwner: Boolean = false,
        blogTranslationState: BlogTranslationUiState? = null,
    ): StatusUiState {
        return StatusUiState(
            status = status,
            role = role,
            logged = logged,
            isOwner = isOwner,
            blogTranslationState = blogTranslationState ?: BlogTranslationUiState(
                support = status.intrinsicBlog.supportTranslate,
                translating = false,
                showingTranslation = false,
                blogTranslation = null,
            ),
            following = following,
        )
    }
}
