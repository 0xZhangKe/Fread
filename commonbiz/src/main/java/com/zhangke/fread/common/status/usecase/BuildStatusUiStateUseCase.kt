package com.zhangke.fread.common.status.usecase

import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import java.text.DateFormat
import java.util.Locale
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
        val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
        val timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM, Locale.getDefault())
        val createdDate = status.datetime
        return StatusUiState(
            status = status,
            role = role,
            displayTime = formatStatusDisplayTime(createdDate),
            specificTime = dateFormat.format(createdDate) + " " + timeFormat.format(createdDate),
            editedTime = status.intrinsicBlog.editedAt?.let {
                dateFormat.format(it) + " " + timeFormat.format(it)
            },
            bottomInteractions = generateBottomInteractionUseCase(status.supportInteraction),
            moreInteractions = generateMoreInteraction(status.supportInteraction),
        )
    }
}
