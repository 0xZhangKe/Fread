package com.zhangke.utopia.pages.sources.search

import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiStateAdapter
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiState
import com.zhangke.utopia.status.source.StatusOwnerAndSources
import com.zhangke.utopia.status.source.StatusSourceOwner
import javax.inject.Inject

data class StatusOwnerAndSourceUiState(
    val owner: StatusSourceOwner,
    val sourceList: List<StatusSourceUiState>,
)

class StatusOwnerAndSourceUiStateAdapter @Inject constructor(
    private val sourceAdapter: StatusSourceUiStateAdapter,
) {

    fun adapt(
        ownerAndSources: StatusOwnerAndSources,
        addEnabled: Boolean,
        removeEnabled: Boolean,
    ): StatusOwnerAndSourceUiState {
        return StatusOwnerAndSourceUiState(
            owner = ownerAndSources.owner,
            sourceList = ownerAndSources.sourceList.map {
                sourceAdapter.adapt(it, addEnabled, removeEnabled)
            }
        )
    }
}
