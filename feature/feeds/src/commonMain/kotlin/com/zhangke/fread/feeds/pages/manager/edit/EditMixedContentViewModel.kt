package com.zhangke.fread.feeds.pages.manager.edit

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.feeds.adapter.StatusSourceUiStateAdapter
import com.zhangke.fread.feeds.composable.StatusSourceUiState
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.uri.FormalUri
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class EditMixedContentViewModel @Inject constructor(
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
    private val configRepo: FreadContentRepo,
    private val statusProvider: StatusProvider,
    @Assisted private val configId: String,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(configId: String): EditMixedContentViewModel
    }

    private val _uiState = MutableStateFlow(LoadableState.loading<EditMixedContentUiState>())
    val uiState: StateFlow<LoadableState<EditMixedContentUiState>> = _uiState.asStateFlow()

    private val _finishScreenFlow = MutableSharedFlow<Unit>()
    val finishScreenFlow: SharedFlow<Unit> = _finishScreenFlow.asSharedFlow()

    init {
        loadFeedsDetail()
    }

    fun onSourceDelete(source: StatusSourceUiState) {
        launchInViewModel {
            val newSourceList = _uiState.value
                .requireSuccessData()
                .sourceList
                .filter { it != source }
            updateSourceList(newSourceList)
            _uiState.updateOnSuccess {
                it.copy(sourceList = newSourceList)
            }
        }
    }

    fun onDeleteFeeds() {
        launchInViewModel {
            configRepo.delete(configId)
            _finishScreenFlow.emit(Unit)
        }
    }

    fun onAddSource(uri: FormalUri) {
        launchInViewModel {
            val sourceList = _uiState.value.requireSuccessData().sourceList.toMutableList()
            if (sourceList.any { it.uri == uri }) return@launchInViewModel
            statusProvider.statusSourceResolver.resolveSourceByUri(null, uri)
                .onSuccess { source ->
                    source?.let {
                        statusSourceUiStateAdapter.adapt(
                            source = it,
                            addEnabled = true,
                            removeEnabled = false
                        )
                    }?.let { sourceList += it }
                }
            updateSourceList(sourceList)
            loadFeedsDetail()
        }
    }

    private suspend fun updateSourceList(sourceList: List<StatusSourceUiState>) {
        getMixedContent()?.copy(sourceUriList = sourceList.map { it.uri })
            ?.let { configRepo.insertContent(it) }
    }

    private fun loadFeedsDetail() {
        launchInViewModel {
            val contentConfig = configRepo.getContent(configId)
            if (contentConfig == null) {
                _uiState.emit(LoadableState.failed(IllegalArgumentException("Unknown Content of $configId")))
                return@launchInViewModel
            }
            if (contentConfig !is MixedContent) {
                _uiState.emit(LoadableState.failed(IllegalArgumentException("Only for Mixed Content")))
                return@launchInViewModel
            }
            val sourceList = contentConfig.sourceUriList.mapNotNull {
                statusProvider.statusSourceResolver.resolveSourceByUri(null, it).getOrNull()
            }.map { source ->
                statusSourceUiStateAdapter.adapt(
                    source,
                    addEnabled = false,
                    removeEnabled = true,
                )
            }
            _uiState.emit(
                LoadableState.success(EditMixedContentUiState(contentConfig.name, sourceList))
            )
        }
    }

    fun onEditName(newName: String) {
        if (newName == _uiState.value.requireSuccessData().name) return
        launchInViewModel {
            val exists = configRepo.checkNameExist(newName)
            if (exists) {
                _uiState.updateOnSuccess {
                    it.copy(errorMessage = "$newName exists!")
                }
                return@launchInViewModel
            }
            getMixedContent()?.copy(name = newName)?.let { configRepo.insertContent(it) }
            _uiState.updateOnSuccess {
                it.copy(name = newName)
            }
        }
    }

    private suspend fun getMixedContent(): MixedContent? {
        return configRepo.getContent(configId) as? MixedContent
    }

    private fun MutableStateFlow<LoadableState<EditMixedContentUiState>>.getUriList(): List<FormalUri> {
        return value.requireSuccessData().sourceList.map { it.uri }
    }
}
