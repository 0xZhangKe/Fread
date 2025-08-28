package com.zhangke.fread.feeds.pages.home.feeds

import com.zhangke.framework.collections.updateItem
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.mixed.MixedStatusRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandleResult
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.author.updateFollowingState
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.model.updateBlogAuthor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MixedContentSubViewModel(
    private val contentRepo: FreadContentRepo,
    private val mixedRepo: MixedStatusRepo,
    statusUpdater: StatusUpdater,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val statusProvider: StatusProvider,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val configId: String,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    private val _uiState = MutableStateFlow(MixedContentUiState.default())
    val uiState = _uiState.asStateFlow()

    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { interaction ->
                when (interaction) {
                    is InteractiveHandleResult.UpdateStatus -> {
                        _uiState.update { state ->
                            state.copy(
                                dataList = state.dataList
                                    .updateItem(interaction.status) { interaction.status }
                            )
                        }
                        mixedRepo.updateStatus(interaction.status)
                    }

                    is InteractiveHandleResult.DeleteStatus -> {
                        _uiState.update { state ->
                            state.copy(
                                dataList = state.dataList
                                    .filter { it.status.id != interaction.statusId }
                            )
                        }
                        mixedRepo.deleteStatus(interaction.statusId)
                    }

                    is InteractiveHandleResult.UpdateFollowState -> {
                        var updatedStatus: StatusUiState? = null
                        _uiState.update { state ->
                            state.copy(
                                dataList = state.dataList.map { status ->
                                    if (status.status.intrinsicBlog.author.uri == interaction.userUri) {
                                        status.updateBlogAuthor {
                                            it.updateFollowingState(interaction.following)
                                        }.also {
                                            updatedStatus = it
                                        }
                                    } else {
                                        status
                                    }
                                }
                            )
                        }
                        updatedStatus?.let { mixedRepo.updateStatus(it) }
                    }
                }
            },
        )
        launchInViewModel {
            _uiState.update { it.copy(initializing = true) }
            val mixedContent = contentRepo.getContent(configId) as? MixedContent
            if (mixedContent == null) {
                _uiState.update { it.copy(pageError = IllegalStateException("Content($configId) does not exists!")) }
            } else {
                _uiState.update { it.copy(content = mixedContent) }
                launch { mixedRepo.refresh(mixedContent) }
                mixedRepo.getLocalStatusFlow(mixedContent)
                    .collect { data ->
                        _uiState.update { it.copy(dataList = data, initializing = false) }
                    }
            }
        }

        launchInViewModel {
            contentRepo.getContentFlow(configId)
                .drop(1)
                .mapNotNull { it as? MixedContent }
                .collect { content ->
                    delay(50)
                    _uiState.update { it.copy(content = content) }
                    mixedRepo.refresh(content)
                }
        }
    }

    fun onContentTitleClick() {
        val mixedContent = uiState.value.content ?: return
        launchInViewModel {
            mutableOpenScreenFlow.emit(EditMixedContentScreen(mixedContent.id))
        }
    }

    fun onRefresh() {
        val content = uiState.value.content ?: return
        if (refreshJob?.isActive == true || loadMoreJob?.isActive == true) return
        refreshJob?.cancel()
        loadMoreJob?.cancel()
        refreshJob = launchInViewModel {
            _uiState.update { it.copy(refreshing = true) }
            mixedRepo.refresh(content)
                .onSuccess {
                    _uiState.update { it.copy(refreshing = false) }
                }.onFailure { t ->
                    _uiState.update { it.copy(refreshing = false) }
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(t)
                }
        }
    }

    fun onLoadMore() {
        val content = uiState.value.content ?: return
        if (refreshJob?.isActive == true || loadMoreJob?.isActive == true) return
        refreshJob?.cancel()
        loadMoreJob?.cancel()
        loadMoreJob = launchInViewModel {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            mixedRepo.loadMoreStatus(content)
                .onSuccess {
                    _uiState.update { it.copy(loadMoreState = LoadState.Idle) }
                }.onFailure { t ->
                    _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(t)
                }
        }
    }
}
