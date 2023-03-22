package com.zhangke.utopia.pages.sources.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.Text
import com.zhangke.utopia.composable.source.maintainer.SourceMaintainerUiState
import com.zhangke.utopia.composable.source.maintainer.SourceMaintainerUiStateAdapter
import com.zhangke.utopia.composable.source.maintainer.StatusSourceUiState
import com.zhangke.utopia.composable.source.maintainer.StatusSourceUiStateAdapter
import com.zhangke.utopia.composable.textOf
import com.zhangke.utopia.db.ChannelRepo
import com.zhangke.utopia.domain.ResolveSourceListByUrisUseCase
import com.zhangke.utopia.repo.StatusSourceRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddSourceViewModel @Inject constructor(
    private val statusSourceRepo: StatusSourceRepo,
    private val maintainerUiStateAdapter: SourceMaintainerUiStateAdapter,
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
    private val channelRepo: ChannelRepo,
    private val resolverSourceListByUris: ResolveSourceListByUrisUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialUiState())
    val uiState: StateFlow<AddSourceUiState> = _uiState.asStateFlow()

    fun onSearchClick(content: String) {
        _uiState.update { it.copy(pendingAdd = true) }
        viewModelScope.launch {
            statusSourceRepo.searchSourceMaintainer(content)
                .onSuccess {
                    if (it == null) {
                        errorState(textOf(R.string.search_result_not_found))
                    } else {
                        normalState(maintainerUiStateAdapter.adapt(it))
                    }
                }.onFailure {
                    errorState(textOf(it.message.orEmpty()))
                }
        }
    }

    fun onAddSourceClick(statusSource: StatusSourceUiState) {
        _uiState.update { currentState ->
            val sourceList = currentState.maintainer?.sourceList
            if (sourceList.isNullOrEmpty()) return@update currentState
            val newSourceList = sourceList.map {
                if (it.uri == statusSource.uri) {
                    it.updateSelected(!it.selected)
                } else {
                    it
                }
            }
            currentState.copy(
                maintainer = currentState.maintainer.copy(sourceList = newSourceList)
            )
        }
    }

    private fun StatusSourceUiState.updateSelected(selected: Boolean): StatusSourceUiState {
        return copy(selected = selected)
    }

    fun onConfirmClick(channelName: String) {
        val pendingAddSource = _uiState.value.maintainer?.sourceList
        if (pendingAddSource.isNullOrEmpty()) return
        viewModelScope.launch {
            pendingAddSource.forEach {
                it.onSaveToLocal()
            }
            channelRepo.insert(channelName, pendingAddSource.map { it.uri })
        }
    }

    private fun normalState(maintainer: SourceMaintainerUiState) {
        _uiState.update {
            it.copy(
                pendingAdd = false,
                searching = false,
                errorMessageText = null,
                maintainer = maintainer,
            )
        }
    }

    private fun errorState(errorMessageText: Text?) {
        _uiState.update {
            it.copy(
                pendingAdd = false,
                searching = false,
                maintainer = null,
                errorMessageText = errorMessageText,
            )
        }
    }

    private fun initialUiState(): AddSourceUiState {
        return AddSourceUiState(
            pendingAdd = true,
            searching = false,
            maintainer = null,
            errorMessageText = null,
        )
    }
}