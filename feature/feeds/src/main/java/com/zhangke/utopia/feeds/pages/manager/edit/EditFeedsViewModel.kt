package com.zhangke.utopia.feeds.pages.manager.edit

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.requireSuccessData
import com.zhangke.framework.composable.updateOnSuccess
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.utopia.feeds.adapter.StatusSourceUiStateAdapter
import com.zhangke.utopia.feeds.composable.StatusSourceUiState
import com.zhangke.utopia.feeds.repo.db.FeedsRepo
import com.zhangke.utopia.status.search.ResolveSourceByUriUseCase
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
    private val feedsRepo: FeedsRepo,
    private val resolveSource: ResolveSourceByUriUseCase,
) : ViewModel() {

    var feedsId: Int = -1

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
            feedsRepo.update(
                id = feedsId,
                name = _uiState.value.requireSuccessData().name,
                uriList = newSourceList.map { it.uri },
            )
            _uiState.updateOnSuccess {
                it.copy(sourceList = newSourceList)
            }
        }
    }

    fun onDeleteFeeds() {
        launchInViewModel {
            feedsRepo.deleteById(feedsId)
            _finishScreenFlow.emit(Unit)
        }
    }

    fun onAddSources(uriList: List<String>) {
        launchInViewModel {
            val sourceList = _uiState.value.requireSuccessData().sourceList.toMutableList()
            val sourceUriList = sourceList.map { it.uri }
            uriList.filter { sourceUriList.contains(it).not() }
                .forEach { uri ->
                    resolveSource(uri).onSuccess { source ->
                        source?.let {
                            statusSourceUiStateAdapter.adapt(
                                source = it,
                                addEnabled = true,
                                removeEnabled = false
                            )
                        }?.let { sourceList += it }
                    }
                }
            feedsRepo.update(
                id = feedsId,
                name = _uiState.value.requireSuccessData().name,
                uriList = sourceList.map { it.uri },
            )
            loadFeedsDetail()
        }
    }

    private fun loadFeedsDetail() {
        launchInViewModel {
            val feeds = feedsRepo.queryById(feedsId)
            if (feeds == null) {
                _uiState.emit(LoadableState.failed(IllegalArgumentException("Unknown Feeds Id:$feedsId")))
                return@launchInViewModel
            }
            val sourceList = feeds.sourceUriList.mapNotNull { resolveSource(it).getOrNull() }
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
            val exists = feedsRepo.checkNameExists(newName)
            if (exists) {
                _uiState.updateOnSuccess {
                    it.copy(errorMessage = "$newName exists!")
                }
                return@launchInViewModel
            }
            feedsRepo.update(
                id = feedsId,
                name = newName,
                uriList = _uiState.fetchUriList(),
            )
            _uiState.updateOnSuccess {
                it.copy(name = newName)
            }
        }
    }

    private fun MutableStateFlow<LoadableState<EditFeedsUiState>>.fetchUriList(): List<String> {
        return value.requireSuccessData().sourceList.map { it.uri }
    }
}
