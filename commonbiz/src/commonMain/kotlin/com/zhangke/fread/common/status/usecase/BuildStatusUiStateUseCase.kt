package com.zhangke.fread.common.status.usecase

import com.zhangke.fread.common.ext.formatDefault
import com.zhangke.fread.common.status.model.BlogTranslationUiState
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject

class BuildStatusUiStateUseCase @Inject constructor(
    private val generateBottomInteractionUseCase: GenerateBottomInteractionUseCase,
    private val generateMoreInteraction: GenerateMoreInteraction,
    private val formatStatusDisplayTime: FormatStatusDisplayTimeUseCase,
) {

    suspend operator fun invoke(
        role: IdentityRole,
        status: Status,
        following: Boolean? = null,
        blogTranslationState: BlogTranslationUiState? = null,
    ): StatusUiState {
        val createdDate = status.datetime
        return StatusUiState(
            status = status,
            role = role,
            displayTime = formatStatusDisplayTime(createdDate),
            // TODO: maybe set date time format
            specificTime = Instant.fromEpochMilliseconds(createdDate).formatDefault(),
            // TODO: maybe set date time format
            editedTime = status.intrinsicBlog.editedAt?.instant?.formatDefault(),
            blogTranslationState = blogTranslationState ?: BlogTranslationUiState(
                support = status.intrinsicBlog.supportTranslate,
                translating = false,
                showingTranslation = false,
                blogTranslation = null,
            ),
            following = following,
            bottomInteractions = generateBottomInteractionUseCase(status.supportInteraction),
            moreInteractions = generateMoreInteraction(status.supportInteraction),
        )
    }
}
