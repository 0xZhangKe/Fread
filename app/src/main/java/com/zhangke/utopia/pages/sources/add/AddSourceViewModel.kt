package com.zhangke.utopia.pages.sources.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.collections.container
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.TextString
import com.zhangke.utopia.composable.textOf
import com.zhangke.utopia.db.FeedsRepo
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiState
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiStateAdapter
import com.zhangke.utopia.status.search.ResolveSourceByUriUseCase
import com.zhangke.utopia.status.source.StatusSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddSourceViewModel @Inject constructor(
    private val resolveSourceUseCase: ResolveSourceByUriUseCase,
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
    private val feedsRepo: FeedsRepo,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialUiState())
    val uiState: StateFlow<AddSourceUiState> = _uiState.asStateFlow()

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: Flow<TextString> = _errorMessageFlow.asSharedFlow()

    fun onAddSources(uris: String) {
        launchInViewModel {
            val uriArray = uris.split(',')
            val sourceList = mutableListOf<StatusSourceUiState>()
            sourceList.addAll(_uiState.value.sourceList)
            uriArray.forEach { uri ->
                resolveSourceUseCase(uri)
                    .onSuccess { source ->
                        source?.takeIf { item -> !sourceList.container { it.uri == item.uri } }
                            ?.toUiState()?.let { sourceList += it }
                    }
            }
            _uiState.update {
                it.copy(
                    sourceList = sourceList
                )
            }
        }
    }

    private fun StatusSource.toUiState(): StatusSourceUiState {
        return statusSourceUiStateAdapter.adapt(
            this,
            addEnabled = false,
            removeEnabled = true,
        )
    }

    fun onRemoveSource(source: StatusSourceUiState) {
        _uiState.update { state ->
            state.copy(
                sourceList = state.sourceList.toMutableList()
                    .also { list ->
                        list.remove(source)
                    }
            )
        }
    }

    fun onConfirmClick(name: String) {
        launchInViewModel {
            val sourceList = _uiState.value.sourceList
            if (sourceList.isEmpty()) {
                _errorMessageFlow.emit(textOf(R.string.add_feeds_page_empty_source_tips))
                return@launchInViewModel
            }
            feedsRepo.insert(name, sourceList.map { it.uri })
        }
    }

    private fun initialUiState(): AddSourceUiState {
        return AddSourceUiState(
            sourceList = emptyList(),
        )
    }
}
