package com.zhangke.fread.bluesky.internal.screen.home

import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.bluesky.BlueskyAccountManager
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BlueskyHomeViewModel(
    private val configId: Long,
    private val contentConfigRepo: ContentConfigRepo,
    private val accountManager: BlueskyAccountManager,
) : SubViewModel() {

    private val _uiState = MutableStateFlow(BlueskyHomeUiState.default())
    val uiState: StateFlow<BlueskyHomeUiState> = _uiState


}
