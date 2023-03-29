package com.zhangke.utopia.composable.source.maintainer

import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class StatusSourceUiStateAdapter @Inject constructor() {

    fun adapt(statusSource: StatusSource): StatusSourceUiState {
        return StatusSourceUiState(
            uri = statusSource.uri,
            nickName = statusSource.nickName,
            description = statusSource.description,
            thumbnail = statusSource.thumbnail,
            selected = false,
            onSaveToLocal = statusSource::saveToLocal,
            onRequestMaintainer = statusSource::requestMaintainer
        )
    }
}