package com.zhangke.fread.bluesky.internal.screen.feeds.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.Log
import com.zhangke.fread.bluesky.internal.content.BlueskyContent
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.GetFollowingFeedsUseCase
import com.zhangke.fread.bluesky.internal.usecase.UpdatePinnedFeedsOrderUseCase
import com.zhangke.fread.common.content.FreadContentRepo
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
    private val contentRepo: FreadContentRepo,
    private val updatePinnedFeedsOrder: UpdatePinnedFeedsOrderUseCase,
    @Assisted private val contentId: String?,
    @Assisted private val role: IdentityRole?,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(
            contentId: String?,
            role: IdentityRole?,
        ): BskyFollowingFeedsViewModel
    }

    private val _uiState = MutableStateFlow(BskyFeedsExplorerUiState.default())
    val uiState = _uiState.asStateFlow()

    private val _snackBarMessage = MutableSharedFlow<TextString>()
    val snackBarMessage = _snackBarMessage.asSharedFlow()

    private var initJob: Job? = null

    private var cachedRole: IdentityRole? = role

    init {
        loadFeedsList(false)
    }

    fun onRefresh() {
        loadFeedsList(true)
    }

    fun onPageResume() {
        loadFeedsList(false)
    }

    fun onFeedsUpdate(feeds: BlueskyFeeds) {
        val feedsList = _uiState.value.followingFeeds
        val exists = feedsList.any { it.id == feeds.id }
        val newFeedsList = if (feeds.pinned) {
            if (exists) {
                feedsList.map {
                    if (it.id == feeds.id) {
                        feeds
                    } else {
                        it
                    }
                }
            } else {
                feedsList + feeds
            }
        } else {
            if (exists) {
                feedsList.filter { it.id != feeds.id }
            } else {
                feedsList
            }
        }
        _uiState.update { it.copy(followingFeeds = newFeedsList) }
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
            val roleResult = getRole()
            if (roleResult.isFailure) {
                _uiState.update {
                    it.copy(
                        initializing = false,
                        refreshing = false,
                        pageError = roleResult.exceptionOrNull(),
                    )
                }
                return@launch
            }
            val role = roleResult.getOrThrow()
            _uiState.update { it.copy(role = role) }
            getFollowingFeeds(role)
                .onSuccess { list ->
                    list.joinToString { it.id }.let { Log.d("F_TEST") { it } }
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

    private suspend fun getRole(): Result<IdentityRole> {
        if (cachedRole != null) return Result.success(cachedRole!!)
        val content = contentId?.let { contentRepo.getContent(it) }?.let { it as? BlueskyContent }
        if (content == null) return Result.failure(IllegalArgumentException("Content not found $contentId"))
        return Result.success(
            IdentityRole(
                accountUri = null,
                baseUrl = content.baseUrl,
            )
        ).onSuccess { this.cachedRole = it }
    }

    fun onFeedsOrderChanged(startIndex: Int, endIndex: Int){
        launchInViewModel {
            updatePinnedFeedsOrder
        }
    }
}
