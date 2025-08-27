package com.zhangke.fread.explore.screens.search.platform

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.emitInViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.search.SearchedPlatform
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

open class SearchPlatformViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    @Assisted private val locator: PlatformLocator,
    @Assisted private val query: String,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {
        fun create(locator: PlatformLocator, query: String): SearchPlatformViewModel
    }

    private val _uiState = MutableStateFlow(SearchedPlatformUiState.default())
    val uiState: StateFlow<SearchedPlatformUiState> get() = _uiState

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow

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
        )?.let { KRouter.route<Screen>(it) }
            ?.let { _openScreenFlow.emitInViewModel(it) }
    }
}
