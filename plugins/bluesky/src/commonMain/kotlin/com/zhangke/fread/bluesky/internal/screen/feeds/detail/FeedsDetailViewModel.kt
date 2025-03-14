package com.zhangke.fread.bluesky.internal.screen.feeds.detail

import androidx.lifecycle.ViewModel
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import me.tatarka.inject.annotations.Inject

class FeedsDetailViewModel @Inject constructor(

): ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,

        ): FeedsDetailViewModel
    }

    private val _uiState = MutableStateFlow(FeedsDetailUiState.default())
    val uiState = _uiState.asStateFlow()
}
