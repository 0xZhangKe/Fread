package com.zhangke.fread.bluesky.internal.screen.home.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.LoadableState
import com.zhangke.framework.composable.updateToFailed
import com.zhangke.framework.composable.updateToLoading
import com.zhangke.framework.composable.updateToSuccess
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentViewModel
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.di.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class BlueskyEditContentViewModel @Inject constructor(
    private val contentRepo: FreadContentRepo,
    @Assisted private val contentId: String,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            contentId: String,
        ): BlueskyEditContentViewModel
    }

    private val _uiState = MutableStateFlow(LoadableState.idle<BlueskyEditContentUiState>())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.updateToLoading()
            val content = contentRepo.getContent(contentId)?.let { it as? BlueskyContent }
            if (content == null) {
                _uiState.updateToFailed(IllegalArgumentException("Content not found $contentId"))
            } else {
                _uiState.updateToSuccess(BlueskyEditContentUiState(content))
            }
        }
    }
}
