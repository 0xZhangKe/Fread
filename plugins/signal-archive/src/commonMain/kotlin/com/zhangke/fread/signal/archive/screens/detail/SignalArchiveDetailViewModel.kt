package com.zhangke.fread.signal.archive.screens.detail

import androidx.lifecycle.ViewModel
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.fread.signal.archive.model.SignalArchiveArticle
import com.zhangke.fread.signal.archive.repo.SignalArchiveRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignalArchiveDetailUiState(
    val article: SignalArchiveArticle? = null,
    val loading: Boolean = false,
    val failed: Boolean = false,
)

class SignalArchiveDetailViewModel(
    private val key: SignalArchiveDetailNavKey,
    private val repo: SignalArchiveRepo,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignalArchiveDetailUiState())
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        if (_uiState.value.loading) return
        _uiState.update { it.copy(loading = true, failed = false) }
        launchInViewModel {
            val article = repo.getArticle(key.baseUrl, key.detailPath).getOrNull()
            _uiState.update { it.copy(article = article, loading = false, failed = article == null) }
        }
    }
}
