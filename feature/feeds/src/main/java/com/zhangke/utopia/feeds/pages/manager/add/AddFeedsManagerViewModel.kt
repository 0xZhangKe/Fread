package com.zhangke.utopia.feeds.pages.manager.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.collections.container
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.ktx.map
import com.zhangke.utopia.common.status.repo.FeedsConfigRepo
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.feeds.adapter.StatusSourceUiStateAdapter
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.platform.BlogPlatform
import com.zhangke.utopia.status.source.StatusSource
import com.zhangke.utopia.status.uri.FormalUri
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
    private val statusProvider: StatusProvider,
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
    private val feedsConfigRepo: FeedsConfigRepo,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(initialViewModelState())

    val uiState: StateFlow<AddFeedsManagerUiState> =
        viewModelState.map(viewModelScope) { it.toUiState() }

    private val _errorMessageFlow = MutableSharedFlow<TextString>()
    val errorMessageFlow: Flow<TextString> = _errorMessageFlow.asSharedFlow()

    private val _finishPage = MutableSharedFlow<Boolean>()
    val finishPage: SharedFlow<Boolean> get() = _finishPage.asSharedFlow()

    private val _loginRecommendPlatform = MutableSharedFlow<List<BlogPlatform>>()
    val loginRecommendPlatform = _loginRecommendPlatform.asSharedFlow()

    fun onAddSources(uriList: List<FormalUri>) {
        launchInViewModel {
            val sourceList = mutableListOf<StatusSource>()
            sourceList.addAll(viewModelState.value.sourceList)
            uriList.forEach { uri ->
                statusProvider.statusSourceResolver.resolveSourceByUri(uri)
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
            val sourceList = currentState.sourceList
            if (sourceList.isEmpty()) {
                _errorMessageFlow.emit(textOf(R.string.add_feeds_page_empty_source_tips))
                return@launchInViewModel
            }
            statusProvider.accountManager.validateAuthOfSourceList(sourceList)
                .onFailure {
                    _errorMessageFlow.emit(textOf(it.message.orEmpty()))
                }.onSuccess {
                    if (it.invalidateList.isEmpty()) {
                        onReadyToAdd()
                    } else {
                        onValidateAuthFailed(it.invalidateList)
                    }
                }
        }
    }

    private suspend fun onValidateAuthFailed(sourceList: List<StatusSource>) {
        statusProvider.platformResolver
            .resolveBySourceUriList(sourceList.map { source -> source.uri })
            .onSuccess {
                _loginRecommendPlatform.emit(it)
            }.onFailure {
                _loginRecommendPlatform.emit(emptyList())
            }
    }

    fun onAuthItemClick(platform: BlogPlatform) {
        launchInViewModel {
            statusProvider.accountManager
                .launchAuthBySource(platform.baseUrl)
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

    private fun onReadyToAdd() {
        val currentState = viewModelState.value
        val sourceUriList = currentState.sourceList.map { it.uri }
        val sourceName = currentState.sourceName
        launchInViewModel {
            feedsConfigRepo.insert(sourceName, sourceUriList)
            _finishPage.emit(true)
        }
    }

    private fun initialViewModelState(): AddSourceViewModelState {
        return AddSourceViewModelState(
            sourceList = emptyList(),
            sourceName = "",
        )
    }

    private fun AddSourceViewModelState.toUiState(): AddFeedsManagerUiState {
        return AddFeedsManagerUiState(
            sourceList = sourceList.map { it.toUiState() },
            sourceName = sourceName,
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
