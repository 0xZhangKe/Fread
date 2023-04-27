package com.zhangke.utopia.pages.search

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.composable.textOf
import com.zhangke.utopia.status.search.GetOwnerAndSourceFromSearchResultUseCase
import com.zhangke.utopia.status.search.StatusProviderSearchUseCase
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.source.StatusSourceOwner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val statusProviderSearchUseCase: StatusProviderSearchUseCase,
    private val getOwnerAndSourceFromSearchResult: GetOwnerAndSourceFromSearchResultUseCase,
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
                        getOwnerAndSourceFromSearchResult(it)
                    }?.onSuccess { pair ->
                        _uiState.update {
                            successUiState(pair.first, pair.second)
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
        owner: StatusSourceOwner,
        sourceList: List<StatusSource>
    ): SearchUiState {
        return SearchUiState(
            errorMessageText = null,
            searchedData = LoadableState.success(
                owner to sourceList.map { it.toUiState() }
            )
        )
    }

    private fun StatusSource.toUiState(): StatusSourceUiState {
        return StatusSourceUiState(
            uri = uri,
            name = name,
            description = description,
            thumbnail = thumbnail,
            selected = false,
        )
    }
}
