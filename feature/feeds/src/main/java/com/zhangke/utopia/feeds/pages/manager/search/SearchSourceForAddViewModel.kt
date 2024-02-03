package com.zhangke.utopia.feeds.pages.manager.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.composable.updateToLoading
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.feeds.adapter.StatusSourceUiStateAdapter
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.source.StatusSource
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun onSearchClick(query: String) {
        _uiState.updateToLoading()
        launchInViewModel {
            statusProvider.searchEngine
                .searchSource(query)
                .onSuccess { list ->
                    _uiState.value = LoadableState.success(list.map { it.toUiState() })
                }.onFailure { e ->
                    _uiState.updateToFailed(e)
                }
        }
    }

    private fun StatusSource.toUiState(): StatusSourceUiState {
        return statusSourceUiStateAdapter.adapt(
            this,
            addEnabled = false,
            removeEnabled = false,
        )
    }
}
