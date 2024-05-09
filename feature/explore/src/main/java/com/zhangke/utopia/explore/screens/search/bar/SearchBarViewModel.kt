package com.zhangke.utopia.explore.screens.search.bar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.model.SearchResultUiState
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.utopia.commonbiz.shared.feeds.handle
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.explore.usecase.BuildSearchResultUiStateUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.account.LoggedAccount
import com.zhangke.utopia.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchBarViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val buildSearchResultUiState: BuildSearchResultUiStateUseCase,
    buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
) : ViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
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
                            if (it.status.status.id != newUiState.status.id) return@map it
                            return@map it.copy(status = newUiState)
                        }
                    )
                }
            },
            followStateUpdater = { _, _ ->

            }
        )
    }
}
