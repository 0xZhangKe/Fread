package com.zhangke.utopia.pages.sources.add

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.domain.RemoveSourceFromOwnerUseCase
import com.zhangke.utopia.pages.feeds.shared.composable.StatusSourceUiState
import com.zhangke.utopia.pages.sources.search.StatusOwnerAndSourceUiStateAdapter
import com.zhangke.utopia.status.search.GetOwnerAndSourceByUriUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class AddSourceViewModel @Inject constructor(
    private val getOwnerAndSourceByUriResult: GetOwnerAndSourceByUriUseCase,
    private val ownerAndSourceAdapter: StatusOwnerAndSourceUiStateAdapter,
    private val removeSourceFromOwner: RemoveSourceFromOwnerUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialUiState())
    val uiState: StateFlow<AddSourceUiState> = _uiState.asStateFlow()

    fun onAddSource(uri: String) {
        launchInViewModel {
            getOwnerAndSourceByUriResult(uri)
                .onSuccess { ownerAndSource ->
                    _uiState.update {
                        val newOne = ownerAndSourceAdapter.adapt(
                            ownerAndSource,
                            addEnabled = false,
                            removeEnabled = true,
                        )
                        it.copy(
                            sourceList = it.sourceList.toMutableList().apply {
                                add(newOne)
                            }
                        )
                    }
                }.onFailure {

                }
        }
    }

    fun onRemoveSource(source: StatusSourceUiState) {
        _uiState.update {
            it.copy(
                sourceList = removeSourceFromOwner(it.sourceList, source)
            )
        }
    }

    fun onConfirmClick(name: String){

    }


//    fun onSearchClick(content: String) {
//        _uiState.update { it.copy(pendingAdd = true) }
//        launchInViewModel {
//            statusSourceRepo.searchSourceMaintainer(content)
//                .onSuccess {
//                    if (it == null) {
//                        errorState(textOf(R.string.search_result_not_found))
//                    } else {
//                        normalState(maintainerUiStateAdapter.adapt(it))
//                    }
//                }.onFailure {
//                    errorState(textOf(it.message.orEmpty()))
//                }
//        }
//    }
//
//    fun onAddSourceClick(statusSource: StatusSourceUiState) {
//        _uiState.update { currentState ->
//            val sourceList = currentState.maintainer?.sourceList
//            if (sourceList.isNullOrEmpty()) return@update currentState
//            val newSourceList = sourceList.map {
//                if (it.uri == statusSource.uri) {
//                    it.updateSelected(!it.selected)
//                } else {
//                    it
//                }
//            }
//            currentState.copy(
//                maintainer = currentState.maintainer.copy(sourceList = newSourceList)
//            )
//        }
//    }
//
//    private fun StatusSourceUiState.updateSelected(selected: Boolean): StatusSourceUiState {
//        return copy(selected = selected)
//    }
//
//    fun onConfirmClick(channelName: String) {
//        val pendingAddSource = _uiState.value.maintainer?.sourceList
//        if (pendingAddSource.isNullOrEmpty()) return
//        viewModelScope.launch {
////            pendingAddSource.forEach {
////                cacheSourceUseCase(it)
////            }
//            channelRepo.insert(channelName, pendingAddSource.map { it.uri })
//        }
//    }
//
//    private fun normalState(maintainer: SourceMaintainerUiState) {
//        _uiState.update {
//            it.copy(
//                pendingAdd = false,
//                searching = false,
//                errorMessageText = null,
//                maintainer = maintainer,
//            )
//        }
//    }
//
//    private fun errorState(errorMessageText: TextString?) {
//        _uiState.update {
//            it.copy(
//                pendingAdd = false,
//                searching = false,
//                maintainer = null,
//                errorMessageText = errorMessageText,
//            )
//        }
//    }

    private fun initialUiState(): AddSourceUiState {
        return AddSourceUiState(
            sourceList = emptyList(),
            errorMessageText = null,
        )
    }
}
