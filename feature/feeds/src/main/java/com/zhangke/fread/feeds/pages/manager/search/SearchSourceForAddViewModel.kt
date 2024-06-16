package com.zhangke.fread.feeds.pages.manager.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.composable.updateToLoading
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.feeds.adapter.StatusSourceUiStateAdapter
import com.zhangke.fread.feeds.composable.StatusSourceUiState
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.source.StatusSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class SearchSourceForAddViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        LoadableState.idle<List<StatusSourceUiState>>()
    )

    val uiState get() = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onSearchClick(query: String) {
        _uiState.updateToLoading()
        launchInViewModel {
            doSearch(query)
                .onSuccess { list ->
                    _uiState.value = LoadableState.success(list.map { it.toUiState() })
                }.onFailure { e ->
                    _uiState.updateToFailed(e)
                }
        }
    }

    fun onQueryChanged(query: String) {
        searchJob?.cancel()
        searchJob = launchInViewModel {
            doSearch(query)
                .onSuccess { list ->
                    _uiState.value = LoadableState.success(list.map { it.toUiState() })
                }
        }
    }

    private suspend fun doSearch(query: String): Result<List<StatusSource>> {
        return statusProvider.searchEngine.searchSource(IdentityRole.nonIdentityRole, query)
    }

    private fun StatusSource.toUiState(): StatusSourceUiState {
        return statusSourceUiStateAdapter.adapt(
            this,
            addEnabled = false,
            removeEnabled = false,
        )
    }
}
