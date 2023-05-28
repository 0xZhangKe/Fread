package com.zhangke.utopia.pages.sources.add.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiState
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiStateAdapter
import com.zhangke.utopia.status.search.SearchStatusSourceUseCase
import com.zhangke.utopia.status.source.StatusSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchSourceForAddViewModel @Inject constructor(
    private val searchUseCase: SearchStatusSourceUseCase,
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SearchSourceForAddUiState(
            addedSourceUriList = emptyList(),
            searchedResult = LoadableState.idle(),
        )
    )

    val uiState get() = _uiState.asStateFlow()

    fun onSearchClick(query: String) {
        _uiState.update {
            it.copy(
                searchedResult = LoadableState.loading()
            )
        }
        launchInViewModel {
            searchUseCase(query)
                .onSuccess { list ->
                    _uiState.update { state ->
                        state.copy(
                            searchedResult = LoadableState.success(list.map { it.toUiState() })
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(searchedResult = LoadableState.failed(e))
                    }
                }
        }
    }

    private fun StatusSource.toUiState(): StatusSourceUiState {
        return statusSourceUiStateAdapter.adapt(
            this,
            addEnabled = true,
            removeEnabled = false,
        )
    }

    fun onAddClick(result: StatusSourceUiState) {
        _uiState.update {
            it.copy(
                addedSourceUriList = it.addedSourceUriList
                    .toMutableList()
                    .also { list ->
                        list.add(result.uri)
                    }
            )
        }
        updateAddableRemove()
    }

    fun onRemoveClick(result: StatusSourceUiState) {
        _uiState.update {
            it.copy(
                addedSourceUriList = it.addedSourceUriList
                    .toMutableList()
                    .also { list ->
                        list.remove(result.uri)
                    }
            )
        }
        updateAddableRemove()
    }

    private fun updateAddableRemove() {
        val currentState = _uiState.value
        val resultList = (currentState.searchedResult as? LoadableState.Success)?.data ?: return
        val newList = resultList.map {
            val addEnable = !currentState.addedSourceUriList.contains(it.uri)
            it.copy(
                addEnabled = addEnable,
                removeEnabled = !addEnable,
            )
        }
        _uiState.update {
            it.copy(
                searchedResult = LoadableState.success(newList)
            )
        }
    }
}
