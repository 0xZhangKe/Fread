package com.zhangke.utopia.common.status.usecase

import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import javax.inject.Inject

class BuildStatusUiStateUseCase @Inject constructor(
    private val generateBottomInteractionUseCase: GenerateBottomInteractionUseCase,
    private val generateMoreInteraction: GenerateMoreInteraction,
    private val formatStatusDisplayTime: FormatStatusDisplayTimeUseCase,
) {

    operator fun invoke(role: IdentityRole, status: Status): StatusUiState {
        return StatusUiState(
            status = status,
            role = role,
            displayTime = formatStatusDisplayTime(status.datetime),
            bottomInteractions = generateBottomInteractionUseCase(status.supportInteraction),
            moreInteractions = generateMoreInteraction(status.supportInteraction),
        )
    }
}
