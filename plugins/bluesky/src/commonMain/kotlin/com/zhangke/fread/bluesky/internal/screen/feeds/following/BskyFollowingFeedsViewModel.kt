package com.zhangke.fread.bluesky.internal.screen.feeds.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.TextString
import com.zhangke.fread.bluesky.internal.usecase.GetFollowingFeedsUseCase
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.IdentityRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class BskyFollowingFeedsViewModel @Inject constructor(
    private val getFollowingFeeds: GetFollowingFeedsUseCase,
    @Assisted private val role: IdentityRole,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            role: IdentityRole,
        ): BskyFollowingFeedsViewModel
    }

    private val _uiState = MutableStateFlow(BskyFeedsExplorerUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    private var cursor: String? = null

    private var initJob: Job? = null

    init {
        loadFeedsList(false)
    }

    fun onRefresh() {
        loadFeedsList(true)
    }

    fun onPageResume(){
        loadFeedsList(false)
    }

    private fun loadFeedsList(refreshing: Boolean) {
        if (initJob?.isActive == true) return
        initJob?.cancel()
        initJob = viewModelScope.launch {
            _uiState.update {
                it.copy(
                    initializing = !refreshing,
                    refreshing = refreshing,
                    pageError = null,
                )
            }
            getFollowingFeeds(role)
                .onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            initializing = false,
                            refreshing = false,
                            followingFeeds = list,
                        )
                    }
                }.onFailure { t ->
                    _uiState.update {
                        it.copy(
                            initializing = false,
                            refreshing = false,
                            pageError = t,
                        )
                    }
                }
        }
    }
}
