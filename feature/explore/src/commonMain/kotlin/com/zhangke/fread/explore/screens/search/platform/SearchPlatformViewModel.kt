package com.zhangke.fread.explore.screens.search.platform

import androidx.lifecycle.ViewModel
import androidx.navigation3.runtime.NavKey
import com.zhangke.framework.composable.emitInViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.search.SearchedPlatform
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
open class SearchPlatformViewModel(
    private val statusProvider: StatusProvider,
    private val locator: PlatformLocator,
    private val query: String,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchedPlatformUiState.default())
    val uiState: StateFlow<SearchedPlatformUiState> get() = _uiState

    private val _openScreenFlow = MutableSharedFlow<NavKey>()
    val openScreenFlow: SharedFlow<NavKey> get() = _openScreenFlow

    init {
        launchInViewModel {
            _uiState.update { it.copy(searching = true) }
            statusProvider.searchEngine
                .searchPlatform(locator, query.trim())
                .collect { results ->
                    _uiState.update {
                        it.copy(
                            searching = false,
                            searchedList = it.searchedList + results,
                        )
                    }
                }
        }
    }

    fun onContentClick(result: SearchedPlatform) {
        val baseUrl = when (result) {
            is SearchedPlatform.Platform -> result.platform.baseUrl

            is SearchedPlatform.Snapshot -> {
                FormalBaseUrl.parse(result.snapshot.domain) ?: return
            }
        }
        val protocol = when (result) {
            is SearchedPlatform.Platform -> result.platform.protocol
            is SearchedPlatform.Snapshot -> result.snapshot.protocol
        }
        statusProvider.screenProvider.getInstanceDetailScreen(
            baseUrl = baseUrl,
            protocol = protocol,
            locator = locator,
        )?.let { _openScreenFlow.emitInViewModel(it) }
    }
}