package com.zhangke.utopia.common.status.model

import com.zhangke.utopia.status.status.model.Status

data class StatusUiState (
    val status: Status,
    val bottomInteractions: List<StatusUiInteraction>,
    val moreInteractions: List<StatusUiInteraction>,
)
