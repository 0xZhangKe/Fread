package com.zhangke.utopia.pages.feeds.shared.adapter

import com.zhangke.utopia.pages.feeds.shared.source.StatusSourceUiState
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

class StatusSourceUiStateAdapter @Inject constructor() {

    fun adapt(
        source: StatusSource,
        addEnabled: Boolean,
        removeEnabled: Boolean,
    ): StatusSourceUiState {
        return StatusSourceUiState(
            uri = source.uri,
            name = source.name,
            description = source.description,
            thumbnail = source.thumbnail,
            addEnabled = addEnabled,
            removeEnabled = removeEnabled,
        )
    }
}
