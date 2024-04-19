package com.zhangke.utopia.feeds.pages.manager.add.mixed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.ktx.map
import com.zhangke.utopia.common.config.UtopiaConfigManager
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.feeds.adapter.StatusSourceUiStateAdapter
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update

@HiltViewModel(assistedFactory = AddMixedFeedsViewModel.Factory::class)
internal class AddMixedFeedsViewModel @AssistedInject constructor(
    private val statusProvider: StatusProvider,
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
    private val configManager: UtopiaConfigManager,
    private val contentConfigRepo: ContentConfigRepo,
    private val configRepo: ContentConfigRepo,
    @Assisted private val statusSource: StatusSource? = null
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(statusSource: StatusSource?): AddMixedFeedsViewModel
    }

    private val viewModelState = MutableStateFlow(initialViewModelState())

    val uiState: StateFlow<AddMixedFeedsUiState> =
        viewModelState.map(viewModelScope) { it.toUiState() }

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: Flow<TextString> = _errorMessageFlow.asSharedFlow()

    private val _addContentSuccessFlow = MutableSharedFlow<Unit>()
    val addContentSuccessFlow: SharedFlow<Unit> get() = _addContentSuccessFlow

    private val _loginRecommendPlatform = MutableSharedFlow<List<BlogPlatform>>()
    val loginRecommendPlatform = _loginRecommendPlatform.asSharedFlow()

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

    fun onAddSource(uri: FormalUri) {
        launchInViewModel {
            val sourceList = mutableListOf<StatusSource>()
            sourceList.addAll(viewModelState.value.sourceList)
            statusProvider.statusSourceResolver.resolveSourceByUri(null, uri)
                .onSuccess { source ->
                    source?.takeIf { item -> !sourceList.container { it.uri == item.uri } }
                        ?.let { sourceList += it }
                }
            viewModelState.update {
                it.copy(sourceList = sourceList)
            }
        }
    }

    fun onRemoveSource(source: StatusSourceUiState) {
        viewModelState.update { state ->
            state.copy(
                sourceList = state.sourceList.filter { it.uri != source.uri }
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
            if (contentConfigRepo.checkNameExist(currentState.sourceName)) {
                _errorMessageFlow.emit(textOf(R.string.add_feeds_page_empty_name_exist))
                return@launchInViewModel
            }
            val sourceList = currentState.sourceList
            if (sourceList.isEmpty()) {
                _errorMessageFlow.emit(textOf(R.string.add_feeds_page_empty_source_tips))
                return@launchInViewModel
            }
            performAddContent()
        }
    }

    private fun performAddContent() {
        val currentState = viewModelState.value
        val sourceUriList = currentState.sourceList.map { it.uri }
        val sourceName = currentState.sourceName
        launchInViewModel {
            val order = configRepo.generateNextOrder()
            val contentConfig = ContentConfig.MixedContent(
                id = 0,
                order = order,
                name = sourceName,
                sourceUriList = sourceUriList,
            )
            contentConfigRepo.insert(contentConfig)
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
            maxNameLength = configManager.contentTitleMaxLength,
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
)
