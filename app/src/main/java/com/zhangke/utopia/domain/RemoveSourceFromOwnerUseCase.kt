package com.zhangke.utopia.domain

import com.zhangke.utopia.pages.sources.search.StatusOwnerAndSourceUiState
import com.zhangke.utopia.pages.sources.search.StatusSourceUiState
import javax.inject.Inject

class RemoveSourceFromOwnerUseCase @Inject constructor() {

    operator fun invoke(
        ownerList: List<StatusOwnerAndSourceUiState>,
        item: StatusSourceUiState,
    ): List<StatusOwnerAndSourceUiState> {
        val newList = mutableListOf<StatusOwnerAndSourceUiState>()
        ownerList.forEach {
            val newSourceList = mutableListOf<StatusSourceUiState>()
            it.sourceList.forEach { source ->
                if (source != item) newSourceList += source
            }
            if (newSourceList.isNotEmpty()) {
                newList += it.copy(sourceList = newSourceList)
            }
        }
        return newList
    }
}