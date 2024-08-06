package com.zhangke.fread.common.status.usecase

import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import javax.inject.Inject

class BuildStatusUiStateUseCase @Inject constructor(
    private val generateBottomInteractionUseCase: GenerateBottomInteractionUseCase,
    private val generateMoreInteraction: GenerateMoreInteraction,
    private val formatStatusDisplayTime: FormatStatusDisplayTimeUseCase,
) {

    operator fun invoke(
        role: IdentityRole,
        status: Status,
    ): StatusUiState {
        return StatusUiState(
            status = status,
            role = role,
            displayTime = formatStatusDisplayTime(status.datetime),
            bottomInteractions = generateBottomInteractionUseCase(status.supportInteraction),
            moreInteractions = generateMoreInteraction(status.supportInteraction),
        )
    }
}
