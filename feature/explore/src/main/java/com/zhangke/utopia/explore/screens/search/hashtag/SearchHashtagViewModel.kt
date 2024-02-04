package com.zhangke.utopia.explore.screens.search.hashtag

import com.zhangke.utopia.explore.screens.search.BaseSearchViewMode
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.Hashtag
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
open class SearchHashtagViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : BaseSearchViewMode<Hashtag>() {

    fun onRefresh(query: String) {
        refresh {
            statusProvider.searchEngine
                .searchHashtag(query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.resultList.size
        if (offset == 0) return
        loadMore {
            statusProvider.searchEngine
                .searchHashtag(query, offset)
        }
    }
}
