package com.zhangke.utopia.feeds.pages.manager.edit

import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.feeds.adapter.StatusSourceUiStateAdapter
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.uri.FormalUri
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel(assistedFactory = EditMixedContentViewModel.Factory::class)
internal class EditMixedContentViewModel @AssistedInject constructor(
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
    private val configRepo: ContentConfigRepo,
    private val statusProvider: StatusProvider,
    @Assisted private val configId: Long,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {

        fun create(configId: Long): EditMixedContentViewModel
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
            configRepo.updateSourceList(configId, newSourceList.map { it.uri })
            _uiState.updateOnSuccess {
                it.copy(sourceList = newSourceList)
            }
        }
    }

    fun onDeleteFeeds() {
        launchInViewModel {
            configRepo.deleteById(configId)
            _finishScreenFlow.emit(Unit)
        }
    }

    fun onAddSource(uri: FormalUri) {
        launchInViewModel {
            val sourceList = _uiState.value.requireSuccessData().sourceList.toMutableList()
            sourceList.map { it.uri }
            if (sourceList.any { it.uri == uri }) return@launchInViewModel
            statusProvider.statusSourceResolver.resolveSourceByUri(uri)
                .onSuccess { source ->
                    source?.let {
                        statusSourceUiStateAdapter.adapt(
                            source = it,
                            addEnabled = true,
                            removeEnabled = false
                        )
                    }?.let { sourceList += it }
                }
            configRepo.updateSourceList(configId, sourceList.map { it.uri })
            loadFeedsDetail()
        }
    }

    private fun loadFeedsDetail() {
        launchInViewModel {
            val contentConfig = configRepo.getConfigById(configId)
            if (contentConfig == null) {
                _uiState.emit(LoadableState.failed(IllegalArgumentException("Unknown Content of $configId")))
                return@launchInViewModel
            }
            if (contentConfig !is ContentConfig.MixedContent) {
                _uiState.emit(LoadableState.failed(IllegalArgumentException("Only for Mixed Content")))
                return@launchInViewModel
            }
            val sourceList = contentConfig.sourceUriList.mapNotNull {
                statusProvider.statusSourceResolver.resolveSourceByUri(it).getOrNull()
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
            configRepo.updateContentName(configId, newName)
            _uiState.updateOnSuccess {
                it.copy(name = newName)
            }
        }
    }

    private fun MutableStateFlow<LoadableState<EditMixedContentUiState>>.getUriList(): List<FormalUri> {
        return value.requireSuccessData().sourceList.map { it.uri }
    }
}
