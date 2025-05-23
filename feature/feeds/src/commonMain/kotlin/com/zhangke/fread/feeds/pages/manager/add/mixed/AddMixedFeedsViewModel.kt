package com.zhangke.fread.feeds.pages.manager.add.mixed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.ktx.map
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.commonbiz.add_feeds_page_empty_name_exist
import com.zhangke.fread.feeds.Res
import com.zhangke.fread.feeds.add_feeds_page_empty_name_tips
import com.zhangke.fread.feeds.add_feeds_page_empty_source_tips
import com.zhangke.fread.feeds.composable.StatusSourceUiState
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.source.StatusSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AddMixedFeedsViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val contentRepo: FreadContentRepo,
    @Assisted private val statusSource: StatusSource? = null
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(statusSource: StatusSource?): AddMixedFeedsViewModel
    }

    private val viewModelState = MutableStateFlow(initialViewModelState())

    val uiState: StateFlow<AddMixedFeedsUiState> =
        viewModelState.map(viewModelScope) { it.toUiState() }

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: Flow<TextString> = _errorMessageFlow.asSharedFlow()

    private val _addContentSuccessFlow = MutableSharedFlow<Unit>()
    val addContentSuccessFlow: SharedFlow<Unit> get() = _addContentSuccessFlow

    init {
        launchInViewModel {
            val initAccountList = statusProvider.accountManager.getAllLoggedAccount()
            statusProvider.accountManager
                .getAllAccountFlow()
                .collect { currentAccountList ->
                    if (initAccountList.isNotEmpty()) return@collect
                    if (currentAccountList.isEmpty()) return@collect
                    onConfirmClick()
                }
        }
    }

    fun onAddSource(source: StatusSource) {
        launchInViewModel {
            val sourceList = mutableListOf<StatusSource>()
            sourceList.addAll(viewModelState.value.sourceList)
            if (!sourceList.container { it.uri == source.uri }) {
                sourceList += source
            }
            viewModelState.update {
                it.copy(sourceList = sourceList)
            }
        }
    }

    fun onRemoveSource(source: StatusSourceUiState) {
        viewModelState.update { state ->
            state.copy(
                sourceList = state.sourceList.filter { it.uri != source.source.uri }
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
                _errorMessageFlow.emit(textOf(Res.string.add_feeds_page_empty_name_tips))
                return@launchInViewModel
            }
            if (contentRepo.checkNameExist(currentState.sourceName)) {
                _errorMessageFlow.emit(textOf(com.zhangke.fread.commonbiz.Res.string.add_feeds_page_empty_name_exist))
                return@launchInViewModel
            }
            val sourceList = currentState.sourceList
            if (sourceList.isEmpty()) {
                _errorMessageFlow.emit(textOf(Res.string.add_feeds_page_empty_source_tips))
                return@launchInViewModel
            }
            performAddContent()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun performAddContent() {
        val currentState = viewModelState.value
        val sourceUriList = currentState.sourceList.map { it.uri }
        val sourceName = currentState.sourceName
        launchInViewModel {
            val order = contentRepo.getMaxOrder() + 1
            val contentConfig = MixedContent(
                id = Uuid.random().toHexString(),
                order = order,
                name = sourceName,
                sourceUriList = sourceUriList,
            )
            contentRepo.insertContent(contentConfig)
            _addContentSuccessFlow.emit(Unit)
        }
    }

    private fun initialViewModelState(): AddSourceViewModelState {
        return AddSourceViewModelState(
            sourceList = if (statusSource == null) emptyList() else listOf(statusSource),
            sourceName = statusSource?.name.orEmpty(),
        )
    }

    private fun AddSourceViewModelState.toUiState(): AddMixedFeedsUiState {
        return AddMixedFeedsUiState(
            sourceList = sourceList.map { it.toUiState() },
            sourceName = sourceName,
            maxNameLength = 8,
        )
    }

    private fun StatusSource.toUiState(
        addEnabled: Boolean = false,
        removeEnabled: Boolean = true,
    ): StatusSourceUiState {
        return StatusSourceUiState(
            source = this,
            addEnabled = addEnabled,
            removeEnabled = removeEnabled,
        )
    }
}

internal data class AddSourceViewModelState(
    val sourceList: List<StatusSource>,
    val sourceName: String,
)
