package com.zhangke.fread.explore.screens.search.platform

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.emitInViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.search.SearchContentResult
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

open class SearchPlatformViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    @Assisted private val role: IdentityRole,
    @Assisted private val query: String,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {
        fun create(role: IdentityRole, query: String): SearchPlatformViewModel
    }

    private val _uiState = MutableStateFlow(SearchedPlatformUiState.default())
    val uiState: StateFlow<SearchedPlatformUiState> get() = _uiState

    private val _openScreenFlow = MutableSharedFlow<Screen>()
    val openScreenFlow: SharedFlow<Screen> get() = _openScreenFlow

    init {
        launchInViewModel {
            _uiState.update { it.copy(searching = true) }
            val list = mutableListOf<SearchContentResult>()
            statusProvider.searchEngine
                .searchContent(role, query)
                .map { it.second }
                .collect { results ->
                    list += results.filter {
                        it !is SearchContentResult.Source
                    }
                }
            _uiState.update { it.copy(searching = false, searchedList = list) }
        }
    }

    fun onContentClick(result: SearchContentResult) {
        val baseUrl = if (result is SearchContentResult.ActivityPubPlatform) {
            result.platform.baseUrl
        } else if (result is SearchContentResult.SearchedPlatformSnapshot) {
            FormalBaseUrl.parse(result.platform.domain) ?: return
        } else {
            return
        }
        statusProvider.screenProvider.getInstanceDetailScreen(
            baseUrl = baseUrl,
            protocol = result.protocol,
        )?.let { KRouter.route<Screen>(it) }
            ?.let { _openScreenFlow.emitInViewModel(it) }
    }
}
