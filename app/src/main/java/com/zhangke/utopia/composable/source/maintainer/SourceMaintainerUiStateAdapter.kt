package com.zhangke.utopia.composable.source.maintainer

import com.zhangke.utopia.status_provider.StatusSourceMaintainer
import javax.inject.Inject

class SourceMaintainerUiStateAdapter @Inject constructor(
    private val sourceAdapter: StatusSourceUiStateAdapter,
) {

    fun adapt(sourceMaintainer: StatusSourceMaintainer): SourceMaintainerUiState {
        return SourceMaintainerUiState(
            url = sourceMaintainer.url,
            name = sourceMaintainer.name,
            description = sourceMaintainer.description,
            thumbnail = sourceMaintainer.thumbnail,
            sourceList = sourceMaintainer.sourceList.map { sourceAdapter.adapt(it) },
        )
    }
}