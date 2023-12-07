package com.zhangke.utopia.feeds.adapter

import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.status.source.StatusSource
import javax.inject.Inject

internal class StatusSourceUiStateAdapter @Inject constructor() {

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
