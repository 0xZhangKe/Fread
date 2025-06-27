package com.zhangke.fread.common.adapter

import com.zhangke.fread.status.model.BlogTranslationUiState
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Inject

class StatusUiStateAdapter @Inject constructor() {

    fun toStatusUiState(
        statusUiStatus: StatusUiState,
        status: Status,
        following: Boolean? = null,
        blogTranslationState: BlogTranslationUiState? = null,
    ): StatusUiState {
        return StatusUiState(
            status = status,
            locator = statusUiStatus.locator,
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

    fun toStatusUiStateSnapshot(
        locator: PlatformLocator,
        status: Status,
        blogTranslationState: BlogTranslationUiState? = null,
    ): StatusUiState {
        return StatusUiState(
            status = status,
            locator = locator,
            logged = false,
            isOwner = false,
            blogTranslationState = blogTranslationState ?: BlogTranslationUiState(
                support = status.intrinsicBlog.supportTranslate,
                translating = false,
                showingTranslation = false,
                blogTranslation = null,
            ),
            following = false,
        )
    }
}
