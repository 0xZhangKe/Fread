package com.zhangke.fread.feeds.pages.manager.add.pre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.tryEmitException
import com.zhangke.framework.coroutines.invokeOnCancel
import com.zhangke.fread.feeds.pages.manager.add.mixed.AddMixedFeedsScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.content.AddContentAction
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.search.SearchContentResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

class PreAddFeedsViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreAddFeedsUiState.default)
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow = _snackBarMessageFlow.asSharedFlow()

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow = _openScreenFlow.asSharedFlow()

    private val _exitScreenFlow = MutableSharedFlow<Unit>()
    val exitScreenFlow = _exitScreenFlow.asSharedFlow()

    private var searchJob: Job? = null
    private var addContentJob: Job? = null
    private var pendingLoginPlatform: BlogPlatform? = null

    private var selectedContentPlatform: BlogPlatform? = null

    init {
        viewModelScope.launch {
            _uiState.update {
                it.copy(allSearchedResult = getSuggestedPlatformSnapshots())
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        if (query.isEmpty()) {
            searchJob?.cancel()
            viewModelScope.launch {
                _uiState.update {
                    it.copy(allSearchedResult = getSuggestedPlatformSnapshots())
                }
            }
            return
        }
        doSearch()
    }

    fun onSearchClick() {
        if (searchJob?.isActive == true) return
        doSearch()
    }

    private fun doSearch() {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    searching = true,
                    searchErrorMessage = null,
                    allSearchedResult = emptyList(),
                )
            }
            statusProvider.searchEngine
                .searchContentNoToken(_uiState.value.query)
                .collect { (query, results) ->
                    if (query == _uiState.value.query) {
                        _uiState.update {
                            val allResult = it.allSearchedResult + results
                            it.copy(
                                allSearchedResult = allResult,
                                searching = allResult.isEmpty(),
                                searchErrorMessage = null,
                            )
                        }
                    }
                }
            _uiState.update { it.copy(searching = false) }
        }
        searchJob?.invokeOnCancel {
            _uiState.update {
                it.copy(searching = false)
            }
        }
    }

    fun onContentClick(result: SearchContentResult) {
        pendingLoginPlatform = null
        addContentJob?.cancel()
        addContentJob = viewModelScope.launch {
            when (result) {
                is SearchContentResult.Source -> {
                    _openScreenFlow.emit(AddMixedFeedsScreen(result.source))
                }

                is SearchContentResult.Platform -> {
                    onPlatformContentAdd(result.platform)
                }

                is SearchContentResult.SearchedPlatformSnapshot -> {
                    _uiState.update { it.copy(loading = true) }
                    statusProvider.platformResolver.resolve(result.platform)
                        .onFailure {
                            _uiState.update { state -> state.copy(loading = false) }
                            _snackBarMessageFlow.tryEmitException(it)
                        }.onSuccess {
                            _uiState.update { state -> state.copy(loading = false) }
                            onPlatformContentAdd(it)
                        }
                }
            }
        }
    }

    fun onLoadingDismissRequest() {
        _uiState.update { it.copy(loading = false) }
    }

    fun onLoginDialogDismissRequest() {
        _uiState.update { it.copy(showLoginDialog = false) }
    }

    fun onLoginClick() {
        val platform = selectedContentPlatform ?: return
        viewModelScope.launch {
            statusProvider.accountManager
                .triggerAuthBySource(platform)
            _exitScreenFlow.emit(Unit)
        }
    }

    private suspend fun onPlatformContentAdd(platform: BlogPlatform) {
        statusProvider.contentManager.addContent(
            platform = platform,
            action = AddContentAction(
                onShowSnackBarMessage = {
                    _snackBarMessageFlow.emit(it)
                },
                onFinishPage = {
                    _exitScreenFlow.emit(Unit)
                },
                onOpenNewPage = {
                    _openScreenFlow.emit(it)
                },
            )
        )
    }

    private suspend fun getSuggestedPlatformSnapshots(): List<SearchContentResult> {
        return statusProvider.platformResolver
            .getSuggestedPlatformList()
            .sortedBy { it.priority }
            .map { SearchContentResult.SearchedPlatformSnapshot(it) }
    }
}
