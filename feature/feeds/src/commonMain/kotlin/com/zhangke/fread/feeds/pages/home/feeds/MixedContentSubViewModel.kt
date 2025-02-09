package com.zhangke.fread.feeds.pages.home.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.textOf
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.mixed.MixedStatusRepo
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.content.MixedContent
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update

class MixedContentSubViewModel(
    private val contentRepo: FreadContentRepo,
    private val mixedRepo: MixedStatusRepo,
    statusUpdater: StatusUpdater,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val configId: String,
) : SubViewModel()

//    IFeedsViewModelController by FeedsViewModelController(
//    statusProvider = statusProvider,
//    statusUpdater = statusUpdater,
//    buildStatusUiState = buildStatusUiState,
//    refactorToNewBlog = refactorToNewBlog,
//)

{

    private val config = StatusConfigurationDefault.config

    private val _uiState = MutableStateFlow(MixedContentUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _openScreen = MutableSharedFlow<Screen>()
    val openScreen = _openScreen.asSharedFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    private var initJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        launchInViewModel {
            _uiState.update { it.copy(initializing = true) }
            val mixedContent = contentRepo.getContent(configId) as? MixedContent
            if (mixedContent == null) {
                _uiState.update { it.copy(pageError = textOf("Content($configId) does not exists!")) }
            } else {
                _uiState.update { it.copy(content = mixedContent) }
                async { mixedRepo.refresh(mixedContent) }
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
            _openScreen.emit(EditMixedContentScreen(mixedContent.id))
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
                }.onFailure {
                    _uiState.update { it.copy(refreshing = false) }
                    _snackBarMessage.emitTextMessageFromThrowable(it)
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
                    _snackBarMessage.emitTextMessageFromThrowable(t)
                }
        }
    }
}
