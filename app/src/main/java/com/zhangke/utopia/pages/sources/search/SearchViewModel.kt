package com.zhangke.utopia.pages.sources.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.composable.textOf
import com.zhangke.utopia.status.search.GetOwnerAndSourceByUriUseCase
import com.zhangke.utopia.status.search.StatusProviderSearchUseCase
import com.zhangke.utopia.status.source.StatusOwnerAndSources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val statusProviderSearchUseCase: StatusProviderSearchUseCase,
    private val getOwnerAndSourceFromSearchResult: GetOwnerAndSourceByUriUseCase,
    private val uiStateAdapter: StatusOwnerAndSourceUiStateAdapter,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SearchUiState(
            errorMessageText = null,
            searchedData = LoadableState.idle(),
        )
    )

    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onSearchClick(content: String) {
        _uiState.update { loadingState() }
        launchInViewModel {
            statusProviderSearchUseCase(content)
                .onSuccess { resultList ->
                    resultList.firstOrNull()?.let {
                        getOwnerAndSourceFromSearchResult(it.uri)
                    }?.onSuccess { ownerResource ->
                        _uiState.update {
                            successUiState(ownerResource)
                        }
                    }?.onFailure { e ->
                        _uiState.update {
                            it.copy(errorMessageText = textOf(e.message.orEmpty()))
                        }
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(errorMessageText = textOf(e.message.orEmpty()))
                    }
                }
        }
    }

    private fun loadingState(): SearchUiState {
        return SearchUiState(errorMessageText = null, searchedData = LoadableState.loading())
    }

    private fun successUiState(
        ownerResource: StatusOwnerAndSources,
    ): SearchUiState {
        return SearchUiState(
            errorMessageText = null,
            searchedData = LoadableState.success(
                uiStateAdapter.adapt(
                    ownerResource,
                    addEnabled = true,
                    removeEnabled = true,
                )
            ),
        )
    }
}
