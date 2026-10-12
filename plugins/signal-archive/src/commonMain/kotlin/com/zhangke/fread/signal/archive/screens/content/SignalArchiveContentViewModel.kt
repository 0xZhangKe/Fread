package com.zhangke.fread.signal.archive.screens.content

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.signal.archive.content.SignalArchiveContent
import com.zhangke.fread.signal.archive.model.SignalArchiveIndex
import com.zhangke.fread.signal.archive.repo.SignalArchiveRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Owns archive navigation only; each feeds tab owns its article loading. */
class SignalArchiveContentViewModel(
    private val content: SignalArchiveContent,
    private val repo: SignalArchiveRepo,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignalArchiveContentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun selectTab(index: Int) {
        if (index !in _uiState.value.index?.children.orEmpty().indices) return
        _uiState.update { it.copy(selectedTab = index) }
    }

    fun refresh() {
        if (_uiState.value.refreshing) return
        _uiState.update { it.copy(refreshing = true, indexFailed = false) }
        launchInViewModel {
            val index = repo.getIndex(content.baseUrl).getOrNull()
            if (index == null) {
                _uiState.update { it.copy(refreshing = false, indexFailed = true) }
            } else {
                updateIndex(index)
            }
        }
    }

    fun updateIndex(index: SignalArchiveIndex) {
        _uiState.update { state ->
            state.copy(
                index = index,
                refreshing = false,
                indexFailed = false,
                selectedTab = state.selectedTab.coerceIn(0, (index.children.size - 1).coerceAtLeast(0)),
                generation = state.generation + 1,
            )
        }
    }
}
