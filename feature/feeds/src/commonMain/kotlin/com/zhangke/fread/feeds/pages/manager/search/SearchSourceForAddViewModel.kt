package com.zhangke.fread.feeds.pages.manager.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.feeds.composable.StatusSourceUiState
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.source.StatusSource
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class SearchSourceForAddViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchForAddUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackbarMessageFlow = MutableSharedFlow<TextString>()
    val snackbarMessageFlow = _snackbarMessageFlow

    private var searchJob: Job? = null


    fun onSearchClick() {
        performSearch()
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query) }
        if (query.isEmpty()) {
            _uiState.update { it.copy(searchedList = emptyList()) }
            return
        }
        performSearch()
    }

    private fun performSearch() {
        if (searchJob?.isActive == true) searchJob?.cancel()
        searchJob = launchInViewModel {
            _uiState.update { it.copy(searching = true) }
            doSearch(_uiState.value.query)
                .onSuccess { list ->
                    _uiState.update { it.copy(searchedList = list, searching = false) }
                }.onFailure { e ->
                    _uiState.update { it.copy(searching = false) }
                    _snackbarMessageFlow.emitTextMessageFromThrowable(e)
                }
        }
    }

    private suspend fun doSearch(query: String): Result<List<StatusSourceUiState>> {
        return statusProvider.searchEngine.searchSourceNoToken(query)
            .map { list -> list.map { it.toUiState() } }
    }

    private fun StatusSource.toUiState(): StatusSourceUiState {
        return StatusSourceUiState(
            source = this,
            addEnabled = false,
            removeEnabled = false,
        )
    }
}
