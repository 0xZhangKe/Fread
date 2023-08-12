package com.zhangke.utopia.feeds.pages.manager.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.ktx.map
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.feeds.adapter.StatusSourceUiStateAdapter
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.feeds.repo.db.FeedsRepo
import com.zhangke.utopia.status.auth.LaunchAuthBySourceListUseCase
import com.zhangke.utopia.status.auth.SourceListAuthValidateUseCase
import com.zhangke.utopia.status.search.ResolveSourceByUriUseCase
import com.zhangke.utopia.status.source.StatusSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class AddFeedsManagerViewModel @Inject constructor(
    private val resolveSourceUseCase: ResolveSourceByUriUseCase,
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
    private val feedsRepo: FeedsRepo,
    private val sourceListAuthValidateUseCase: SourceListAuthValidateUseCase,
    private val launchAuthBySourceListUseCase: LaunchAuthBySourceListUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(initialViewModelState())

    val uiState: StateFlow<AddFeedsManagerUiState> =
        viewModelState.map(viewModelScope) { it.toUiState() }

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: Flow<TextString> = _errorMessageFlow.asSharedFlow()

    private val _finishPage = MutableSharedFlow<Boolean>()
    val finishPage: SharedFlow<Boolean> get() = _finishPage.asSharedFlow()

    fun onAddSources(uriList: List<String>) {
        launchInViewModel {
            val sourceList = mutableListOf<StatusSource>()
            sourceList.addAll(viewModelState.value.sourceList)
            uriList.forEach { uri ->
                resolveSourceUseCase(uri)
                    .onSuccess { source ->
                        source?.takeIf { item -> !sourceList.container { it.uri == item.uri } }
                            ?.let { sourceList += it }
                    }
            }
            viewModelState.update {
                it.copy(sourceList = sourceList)
            }
        }
    }

    fun onRemoveSource(source: StatusSourceUiState) {
        viewModelState.update { state ->
            state.copy(
                sourceList = state.sourceList.filter { it.uri.toString() != source.uri }
            )
        }
    }

    fun onSourceNameInput(name: String) {
        viewModelState.update {
            it.copy(sourceName = name)
        }
    }

    fun onConfirmClick() {
        launchInViewModel {
            val currentState = viewModelState.value
            if (currentState.sourceName.isEmpty()) {
                _errorMessageFlow.emit(textOf(R.string.add_feeds_page_empty_name_tips))
                return@launchInViewModel
            }
            val sourceList = currentState.sourceList
            if (sourceList.isEmpty()) {
                _errorMessageFlow.emit(textOf(R.string.add_feeds_page_empty_source_tips))
                return@launchInViewModel
            }
            sourceListAuthValidateUseCase(sourceList)
                .onFailure {
                    _errorMessageFlow.emit(textOf(it.message.orEmpty()))
                }.onSuccess {
                    if (it.invalidateList.isEmpty()) {
                        onReadyToAdd()
                    } else {
                        viewModelState.update { state ->
                            state.copy(
                                showChooseSourceDialog = true,
                                invalidateSourceList = it.invalidateList,
                            )
                        }
                    }
                }
        }
    }

    fun onAuthItemClick(source: StatusSourceUiState) {
        val sourceModel = viewModelState.value.invalidateSourceList
            .first { it.uri.toString() == source.uri }
        launchInViewModel {
            launchAuthBySourceListUseCase(sourceModel)
                .onSuccess {
                    _errorMessageFlow.emit(
                        textOf(com.zhangke.utopia.commonbiz.R.string.auth_success)
                    )
                    onConfirmClick()
                }.onFailure {
                    _errorMessageFlow.emit(
                        textOf(com.zhangke.utopia.commonbiz.R.string.auth_failed)
                    )
                }
        }
    }

    fun onChooseDialogDismissRequest() {
        viewModelState.update {
            it.copy(showChooseSourceDialog = false)
        }
    }

    private fun onReadyToAdd() {
        val currentState = viewModelState.value
        val sourceUriList = currentState.sourceList.map { it.uri.toString() }
        val sourceName = currentState.sourceName
        launchInViewModel {
            feedsRepo.insert(sourceName, sourceUriList)
            _finishPage.emit(true)
        }
    }

    private fun initialViewModelState(): AddSourceViewModelState {
        return AddSourceViewModelState(
            sourceList = emptyList(),
            sourceName = "",
            showChooseSourceDialog = false,
            invalidateSourceList = emptyList(),
        )
    }

    private fun AddSourceViewModelState.toUiState(): AddFeedsManagerUiState {
        return AddFeedsManagerUiState(
            sourceList = sourceList.map { it.toUiState() },
            sourceName = sourceName,
            showChooseSourceDialog = showChooseSourceDialog,
            invalidateSourceList = invalidateSourceList.map { it.toUiState(removeEnabled = false) }
        )
    }

    private fun StatusSource.toUiState(
        addEnabled: Boolean = false,
        removeEnabled: Boolean = true,
    ): StatusSourceUiState {
        return statusSourceUiStateAdapter.adapt(
            this,
            addEnabled = addEnabled,
            removeEnabled = removeEnabled,
        )
    }
}

internal data class AddSourceViewModelState(
    val sourceList: List<StatusSource>,
    val sourceName: String,
    val showChooseSourceDialog: Boolean,
    val invalidateSourceList: List<StatusSource>,
)
