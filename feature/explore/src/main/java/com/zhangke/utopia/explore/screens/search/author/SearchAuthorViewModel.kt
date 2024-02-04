package com.zhangke.utopia.explore.screens.search.author

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.explore.screens.search.BaseSearchViewMode
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
open class SearchAuthorViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : BaseSearchViewMode<BlogAuthor>() {

    private val _openScreenFlow = MutableSharedFlow<Any>()
    val openScreenFlow: SharedFlow<Any> get() = _openScreenFlow

    fun onRefresh(query: String) {
        refresh {
            statusProvider.searchEngine.searchAuthor(query, null)
        }
    }

    fun onLoadMore(query: String) {
        val offset = uiState.value.resultList.size
        if (offset == 0) return
        loadMore {
            statusProvider.searchEngine.searchAuthor(query, offset)
        }
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        val route = statusProvider.screenProvider.getUserDetailRoute(blogAuthor.uri) ?: return
        launchInViewModel {
            _openScreenFlow.emit(route)
        }
    }
}
