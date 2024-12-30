package com.zhangke.fread.bluesky.internal.screen.feeds.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.fread.bluesky.internal.client.BlueskyClientManager
import com.zhangke.fread.bluesky.internal.usecase.GetFollowingFeedsUseCase
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class BskyFeedsExplorerViewModel @Inject constructor(
    private val clientManager: BlueskyClientManager,
    private val getFollowingFeeds: GetFollowingFeedsUseCase,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
        ): BskyFeedsExplorerViewModel
    }

    private val _uiState = MutableStateFlow(BskyFeedsExplorerUiState.default())
    val uiState = _uiState.asStateFlow()

    init {

    }

    private fun loadFeedsList(){
        viewModelScope.launch {
            getFollowingFeeds(role)
//            clientManager.getClient(role)
//                .getSuggestedFeedsCatching()
        }
    }
}
