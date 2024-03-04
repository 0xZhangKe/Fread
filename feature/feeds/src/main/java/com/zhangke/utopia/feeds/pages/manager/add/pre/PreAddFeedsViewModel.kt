package com.zhangke.utopia.feeds.pages.manager.add.pre

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.feeds.pages.manager.add.AddFeedsManagerScreen
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.search.SearchContentResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PreAddFeedsViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PreAddFeedsUiState(
            query = "",
            allSearchedResult = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        doSearch()
    }

    fun onSearchClick() {
        doSearch(true)
    }

    fun onContentClick(result: SearchContentResult) {
        launchInViewModel {
            if (result is SearchContentResult.Source) {
                _openScreenFlow.emit(AddFeedsManagerScreen(result.source))
            }
        }
    }

    private fun doSearch(showErrorMessage: Boolean = false) {
        searchJob?.cancel()
        searchJob = launchInViewModel {
            statusProvider.searchEngine
                .searchContent(_uiState.value.query)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(allSearchedResult = list)
                    }
                }.onFailure { e ->
                    if (showErrorMessage) {
                        e.message?.let { textOf(it) }
                            ?.let { _snackBarMessageFlow.emit(it) }
                    }
                }
        }
    }
}
