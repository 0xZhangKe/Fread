package com.zhangke.utopia.explore.screens.search.bar

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.model.SearchResultUiState
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.explore.usecase.BuildSearchResultUiStateUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.status.model.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchBarViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val buildSearchResultUiState: BuildSearchResultUiStateUseCase,
) : ViewModel() {

    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow(
        SearchBarUiState(
            query = "",
            resultList = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: SharedFlow<TextString> = _errorMessageFlow

    private val _openScreenFlow = MutableSharedFlow<Any>()
    val openScreenFlow: SharedFlow<Any> get() = _openScreenFlow

    fun onSearchQueryChanged(query: String) {
        if (query == _uiState.value.query) return
        if (query.isEmpty()) {
            _uiState.update { it.copy(query = "", resultList = emptyList()) }
            return
        }
        _uiState.update {
            it.copy(query = query)
        }
        searchJob?.cancel()
        searchJob = launchInViewModel {
            statusProvider.searchEngine
                .search(query)
                .map { list ->
                    list.map { buildSearchResultUiState(it) }
                }.onSuccess { searchResult ->
                    _uiState.update {
                        it.copy(resultList = searchResult)
                    }
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
                    val currentValue = _uiState.value
                    val newResult = currentValue.resultList.map {
                        if (it !is SearchResultUiState.SearchedStatus) return@map it
                        if (it.status.status.id != status.id) return@map it
                        return@map it.copy(status = newStatus)
                    }
                    _uiState.value = currentValue.copy(
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
