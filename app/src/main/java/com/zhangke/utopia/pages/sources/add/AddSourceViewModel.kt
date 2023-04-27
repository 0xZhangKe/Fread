package com.zhangke.utopia.pages.sources.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.R
import com.zhangke.utopia.composable.TextString
import com.zhangke.utopia.composable.source.maintainer.StatusSourceUiState
import com.zhangke.utopia.composable.source.maintainer.StatusSourceUiStateAdapter
import com.zhangke.utopia.composable.textOf
import com.zhangke.utopia.db.ChannelRepo
import com.zhangke.utopia.domain.ResolveSourceListByUrisUseCase
import com.zhangke.utopia.repo.StatusSourceRepo
import com.zhangke.utopia.status.domain.CacheSourceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddSourceViewModel @Inject constructor(
    private val statusSourceRepo: StatusSourceRepo,
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
    private val channelRepo: ChannelRepo,
    private val resolverSourceListByUris: ResolveSourceListByUrisUseCase,
    private val cacheSourceUseCase: CacheSourceUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialUiState())
    val uiState: StateFlow<AddSourceUiState> = _uiState.asStateFlow()

    fun onSearchClick(content: String) {
        _uiState.update { it.copy(pendingAdd = true) }
        launchInViewModel {
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
//            pendingAddSource.forEach {
//                cacheSourceUseCase(it)
//            }
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

    private fun errorState(errorMessageText: TextString?) {
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