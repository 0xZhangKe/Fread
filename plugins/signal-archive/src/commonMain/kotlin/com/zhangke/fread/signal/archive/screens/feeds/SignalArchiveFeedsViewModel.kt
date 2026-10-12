package com.zhangke.fread.signal.archive.screens.feeds

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.signal.archive.model.SignalArchiveIndex
import com.zhangke.fread.signal.archive.repo.SignalArchiveHttpException
import com.zhangke.fread.signal.archive.repo.SignalArchiveRepo
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class SignalArchiveFeedsViewModel(
    private var info: SignalArchiveFeedsTabInfo,
    private val repo: SignalArchiveRepo,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignalArchiveFeedsUiState())
    val uiState = _uiState.asStateFlow()
    private val _indexUpdates = Channel<SignalArchiveIndex>(Channel.BUFFERED)
    val indexUpdates = _indexUpdates.receiveAsFlow()
    private var pageJob: Job? = null
    private var refreshJob: Job? = null
    private var revision = 0
    private var started = false
    private var recoveredFromNotFound = false

    fun updateTab(newInfo: SignalArchiveFeedsTabInfo) {
        if (info == newInfo) return
        pageJob?.cancel()
        refreshJob?.cancel()
        revision++
        info = newInfo
        started = false
        _uiState.value = SignalArchiveFeedsUiState()
    }

    fun ensureLoaded() {
        if (started) return
        started = true
        loadMore()
    }

    fun hasMorePages(): Boolean = _uiState.value.nextPage < info.node.pages.size

    fun acceptIndex(index: SignalArchiveIndex) {
        val node = index.children.getOrNull(info.nodeIndex)
        if (node == null) {
            pageJob?.cancel()
            revision++
            _uiState.value = SignalArchiveFeedsUiState()
            return
        }
        updateTab(info.copy(node = node, generation = info.generation + 1))
        ensureLoaded()
    }

    fun refresh() {
        recoveredFromNotFound = false
        refreshIndex()
    }

    private fun refreshIndex() {
        if (_uiState.value.refreshing) return
        pageJob?.cancel()
        revision++
        _uiState.update { it.copy(refreshing = true, loading = false, failed = false, indexFailed = false) }
        refreshJob = launchInViewModel {
            val index = repo.getIndex(info.baseUrl).getOrNull()
            if (index == null) {
                _uiState.update { it.copy(refreshing = false, failed = true, indexFailed = true) }
            } else {
                // The container updates navigation and supplies the new node snapshot back.
                _indexUpdates.send(index)
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.loading || state.refreshing) return
        val path = info.node.pages.getOrNull(state.nextPage) ?: return
        val requestRevision = revision
        _uiState.update { it.copy(loading = true, failed = false, indexFailed = false) }
        pageJob = launchInViewModel {
            val result = repo.getPage(info.baseUrl, path)
            if (revision != requestRevision) return@launchInViewModel
            val page = result.getOrNull()
            if (page == null) {
                _uiState.update { it.copy(loading = false, failed = true) }
                if ((result.exceptionOrNull() as? SignalArchiveHttpException)?.statusCode == 404 &&
                    !recoveredFromNotFound
                ) {
                    recoveredFromNotFound = true
                    refreshIndex()
                }
            } else {
                _uiState.update {
                    it.copy(
                        items = (state.items + page.items).distinctBy { article -> article.id },
                        nextPage = state.nextPage + 1,
                        loading = false,
                        failed = false,
                    )
                }
            }
        }
    }

    override fun onCleared() {
        _indexUpdates.close()
        super.onCleared()
    }
}
