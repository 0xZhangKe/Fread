package com.zhangke.fread.feeds.adapter

import com.zhangke.fread.feeds.composable.StatusSourceUiState
import com.zhangke.fread.status.source.StatusSource
import me.tatarka.inject.annotations.Inject

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
