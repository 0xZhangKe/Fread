package com.zhangke.utopia.explore.screens.search.status

import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.model.SearchResultUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.explore.screens.search.BaseSearchViewMode
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.status.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

@HiltViewModel
open class SearchStatusViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : BaseSearchViewMode<StatusUiState>() {

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    private val _openScreenFlow = MutableSharedFlow<Any>()
    val openScreenFlow: SharedFlow<Any> get() = _openScreenFlow

    fun onRefresh(query: String) {
        refresh {
            statusProvider.searchEngine
                .searchStatus(query, null)
                .map { list ->
                    list.map { buildStatusUiState(it) }
                }
        }
    }

    fun onLoadMore(query: String) {
        val latestId = uiState.value.resultList.lastOrNull()?.status?.id ?: return
        loadMore {
            statusProvider.searchEngine
                .searchStatus(query, latestId)
                .map { list ->
                    list.map { buildStatusUiState(it) }
                }
        }
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) =
        launchInViewModel {
            if (uiInteraction is StatusUiInteraction.Comment) {
                statusProvider.screenProvider
                    .getReplyBlogScreen(status.intrinsicBlog)
                    ?.let {
                        _openScreenFlow.emit(it)
                    }
                return@launchInViewModel
            }
            val interaction = uiInteraction.statusInteraction ?: return@launchInViewModel
            statusProvider.statusResolver
                .interactive(status, interaction)
                .map { buildStatusUiState(it) }
                .onSuccess { newStatus ->
                    val currentValue = uiState.value
                    val newResult = currentValue.resultList.map {
                        if (it.status.id != status.id) return@map it
                        return@map newStatus
                    }
                    mutableUiState.value = currentValue.copy(
                        resultList = newResult
                    )
                }.onFailure { e ->
                    e.message?.let { message ->
                        _errorMessageFlow.emit(textOf(message))
                    }
                }
        }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        val route = statusProvider.screenProvider.getUserDetailRoute(blogAuthor.uri) ?: return
        launchInViewModel {
            _openScreenFlow.emit(route)
        }
    }
}
