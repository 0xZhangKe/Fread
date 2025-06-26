package com.zhangke.fread.activitypub.app.internal.screen.user.tags

import androidx.lifecycle.ViewModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class TagListViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val activityPubTagAdapter: ActivityPubTagAdapter,
    @Assisted private val locator: PlatformLocator,
) : ViewModel() {

    fun interface Factory : ViewModelFactory {

        fun create(locator: PlatformLocator): TagListViewModel
    }

    private val _uiState = MutableStateFlow(TagListUiState.default(locator))
    val uiState: StateFlow<TagListUiState> = _uiState

    private val _snackBarMessageFlow = MutableSharedFlow<TextString>()
    val snackBarMessageFlow: SharedFlow<TextString> = _snackBarMessageFlow

    private var nextMaxId: String? = null
    private var initializingJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        initializingFollowedTags()
    }

    fun onRefresh() {
        initializingFollowedTags()
    }

    private fun initializingFollowedTags() {
        if (initializingJob?.isActive == true) return
        loadMoreJob?.cancel()
        launchInViewModel {
            nextMaxId = null
            _uiState.update { it.copy(refreshing = true) }
            fetchFollowedTags()
                .onFailure { t ->
                    _uiState.update { it.copy(refreshing = false) }
                    _snackBarMessageFlow.emitTextMessageFromThrowable(t)
                }.onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            refreshing = false,
                            tags = list,
                        )
                    }
                }
        }
    }

    fun onLoadMore() {
        if (initializingJob?.isActive == true) return
        if (loadMoreJob?.isActive == true) return
        val nextMaxId = nextMaxId ?: return
        if (nextMaxId.isEmpty()) return
        launchInViewModel {
            _uiState.update { it.copy(loadState = LoadState.Loading) }
            fetchFollowedTags(nextMaxId)
                .onFailure { t ->
                    _uiState.update { it.copy(loadState = LoadState.Failed(t.toTextStringOrNull())) }
                }.onSuccess { list ->
                    _uiState.update {
                        it.copy(
                            loadState = LoadState.Idle,
                            tags = it.tags + list,
                        )
                    }
                }
        }
    }

    private suspend fun fetchFollowedTags(maxId: String? = null): Result<List<Hashtag>> {
        return clientManager.getClient(locator)
            .accountRepo
            .getFollowedTags(maxId = maxId)
            .map { pagingResult ->
                nextMaxId = pagingResult.pagingInfo.nextMaxId
                pagingResult.data.map { activityPubTagAdapter.adapt(it) }
            }
    }
}
