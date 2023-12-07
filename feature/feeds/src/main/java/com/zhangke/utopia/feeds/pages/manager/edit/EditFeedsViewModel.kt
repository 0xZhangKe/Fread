package com.zhangke.utopia.feeds.pages.manager.edit

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.repo.FeedsConfigRepo
import com.zhangke.utopia.feeds.adapter.StatusSourceUiStateAdapter
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.uri.StatusProviderUri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
internal class EditFeedsViewModel @Inject constructor(
    private val statusSourceUiStateAdapter: StatusSourceUiStateAdapter,
    private val feedsConfigRepo: FeedsConfigRepo,
    private val statusProvider: StatusProvider,
) : ViewModel() {

    var feedsId: Long = -1L

    private val _uiState = MutableStateFlow(LoadableState.loading<EditFeedsUiState>())
    val uiState: StateFlow<LoadableState<EditFeedsUiState>> = _uiState.asStateFlow()

    private val _finishScreenFlow = MutableSharedFlow<Unit>()
    val finishScreenFlow: SharedFlow<Unit> = _finishScreenFlow.asSharedFlow()

    fun onPageResume() {
        loadFeedsDetail()
    }

    fun onSourceDelete(source: StatusSourceUiState) {
        launchInViewModel {
            val newSourceList = _uiState.value
                .requireSuccessData()
                .sourceList
                .filter { it != source }
            feedsConfigRepo.insertOrReplace(
                FeedsConfig(
                    id = feedsId,
                    name = _uiState.value.requireSuccessData().name,
                    sourceUriList = newSourceList.map { it.uri },
                )
            )
            _uiState.updateOnSuccess {
                it.copy(sourceList = newSourceList)
            }
        }
    }

    fun onDeleteFeeds() {
        launchInViewModel {
            feedsConfigRepo.deleteById(feedsId)
            _finishScreenFlow.emit(Unit)
        }
    }

    fun onAddSources(uriList: List<StatusProviderUri>) {
        launchInViewModel {
            val sourceList = _uiState.value.requireSuccessData().sourceList.toMutableList()
            val sourceUriList = sourceList.map { it.uri }
            uriList.filter { sourceUriList.contains(it).not() }
                .forEach { uri ->
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
                }
            feedsConfigRepo.insertOrReplace(
                FeedsConfig(
                    id = feedsId,
                    name = _uiState.value.requireSuccessData().name,
                    sourceUriList = sourceList.map { it.uri },
                )
            )
            loadFeedsDetail()
        }
    }

    private fun loadFeedsDetail() {
        launchInViewModel {
            val feeds = feedsConfigRepo.getConfigById(feedsId)
            if (feeds == null) {
                _uiState.emit(LoadableState.failed(IllegalArgumentException("Unknown Feeds Id:$feedsId")))
                return@launchInViewModel
            }
            val sourceList = feeds.sourceUriList.mapNotNull {
                statusProvider.statusSourceResolver.resolveSourceByUri(it).getOrNull()
            }
                .map { source ->
                    statusSourceUiStateAdapter.adapt(
                        source,
                        addEnabled = false,
                        removeEnabled = true,
                    )
                }
            _uiState.emit(
                LoadableState.success(EditFeedsUiState(feeds.name, sourceList))
            )
        }
    }

    fun onEditName(newName: String) {
        if (newName == _uiState.value.requireSuccessData().name) return
        launchInViewModel {
            val exists = feedsConfigRepo.checkNameExists(newName)
            if (exists) {
                _uiState.updateOnSuccess {
                    it.copy(errorMessage = "$newName exists!")
                }
                return@launchInViewModel
            }
            feedsConfigRepo.insertOrReplace(
                FeedsConfig(
                    id = feedsId,
                    name = newName,
                    sourceUriList = _uiState.getUriList(),
                )
            )
            _uiState.updateOnSuccess {
                it.copy(name = newName)
            }
        }
    }

    private fun MutableStateFlow<LoadableState<EditFeedsUiState>>.getUriList(): List<StatusProviderUri> {
        return value.requireSuccessData().sourceList.map { it.uri }
    }
}
