package com.zhangke.utopia.explore.screens.search.platform

import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.explore.screens.search.BaseSearchViewMode
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.platform.BlogPlatform
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class SearchPlatformViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : BaseSearchViewMode<BlogPlatform>() {

    fun onRefresh(query: String) {
        refresh {
            statusProvider.searchEngine
                .searchPlatform(query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.resultList.size
        if (offset == 0) return
        loadMore {
            statusProvider.searchEngine
                .searchPlatform(query, offset)
        }
    }
}
