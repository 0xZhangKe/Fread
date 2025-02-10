package com.zhangke.fread.explore.screens.search.bar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.model.SearchResultUiState
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.handle
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.explore.usecase.BuildSearchResultUiStateUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.account.LoggedAccount
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.richtext.preParse
import com.zhangke.fread.status.search.SearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

class SearchBarViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    statusUpdater: StatusUpdater,
    private val buildSearchResultUiState: BuildSearchResultUiStateUseCase,
    statusUiStateAdapter: StatusUiStateAdapter,
    refactorToNewStatus: RefactorToNewStatusUseCase,
) : ViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    var selectedAccount: LoggedAccount? = null
        set(value) {
            field = value
            _uiState.update { it.copy(role = role) }
        }

    private val role: IdentityRole
        get() {
            val accountUri = selectedAccount?.uri
            return IdentityRole(accountUri, null)
        }

    private var searchJob: Job? = null

    private val _uiState = MutableStateFlow(
        SearchBarUiState(
            role = role,
            query = "",
            resultList = emptyList(),
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { it.handleResult() },
        )
    }

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
                .search(role, query)
                .map { list ->
                    list.filterIsInstance<SearchResult.SearchedStatus>()
                        .map { it.status }
                        .preParse()
                    list
                }.map { list ->
                    list.map { buildSearchResultUiState(role, it) }
                }.onSuccess { searchResult ->
                    _uiState.update {
                        it.copy(resultList = searchResult)
                    }
                }
        }
    }

    private suspend fun InteractiveHandleResult.handleResult() {
        handle(
            uiStatusUpdater = { newUiState ->
                _uiState.update { currentUiState ->
                    currentUiState.copy(
                        resultList = currentUiState.resultList.map {
                            if (it !is SearchResultUiState.SearchedStatus) return@map it
                            if (it.status.status.intrinsicBlog.id != newUiState.status.intrinsicBlog.id) return@map it
                            return@map it.copy(status = newUiState)
                        }
                    )
                }
            },
            deleteStatus = { statusId ->
                _uiState.update { state ->
                    state.copy(
                        resultList = state.resultList.filter {
                            when (it) {
                                is SearchResultUiState.SearchedStatus -> {
                                    it.status.status.id != statusId
                                }

                                else -> true
                            }
                        }
                    )
                }
            },
            followStateUpdater = { _, _ ->

            }
        )
    }
}
