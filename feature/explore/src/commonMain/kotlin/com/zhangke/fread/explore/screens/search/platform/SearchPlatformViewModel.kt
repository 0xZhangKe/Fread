package com.zhangke.fread.explore.screens.search.platform

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.search.SearchContentResult
import kotlinx.coroutines.flow.MutableStateFlow
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

    init {
        launchInViewModel {
            _uiState.update { it.copy(searching = true) }
            val list = mutableListOf<SearchContentResult>()
            statusProvider.searchEngine
                .searchContent(role, query)
                .map { it.second }
                .collect {
                    list += it
                }
            _uiState.update { it.copy(searching = false, searchedList = list) }
        }
    }
}
