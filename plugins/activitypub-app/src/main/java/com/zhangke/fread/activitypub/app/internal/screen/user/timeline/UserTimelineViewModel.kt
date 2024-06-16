package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.composable.toTextStringOrNull
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.utils.LoadState
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.IInteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.InteractiveHandler
import com.zhangke.fread.commonbiz.shared.feeds.handle
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.richtext.preParseRichText
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class UserTimelineViewModel @AssistedInject constructor(
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    val tabType: UserTimelineTabType,
    val role: IdentityRole,
    val webFinger: WebFinger,
) : SubViewModel(), IInteractiveHandler by InteractiveHandler(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val _uiState = MutableStateFlow(UserTimelineUiState.default)
    val uiState: StateFlow<UserTimelineUiState> = _uiState

    private var iniJob: Job? = null
    private var refreshJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        initInteractiveHandler(
            coroutineScope = viewModelScope,
            onInteractiveHandleResult = { result ->
                result.handle(
                    uiStatusUpdater = {
                        updateStatus(it)
                    },
                    followStateUpdater = { _, _ -> }
                )
            },
        )
        initFeeds()
    }

    private fun initFeeds() {
        iniJob = launchInViewModel {
            _uiState.update { it.copy(showPagingLoadingPlaceholder = true) }
            loadUserTimeline()
                .onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            showPagingLoadingPlaceholder = false,
                            feeds = data,
                        )
                    }
                }.onFailure { t ->
                    _uiState.update {
                        it.copy(
                            showPagingLoadingPlaceholder = false,
                            pageErrorContent = t.toTextStringOrNull(),
                        )
                    }
                }
        }
    }

    fun onRefresh() {
        if (iniJob?.isActive == true) return
        refreshJob?.cancel()
        refreshJob = launchInViewModel {
            _uiState.update { it.copy(refreshing = true) }
            loadUserTimeline()
                .onFailure {
                    _uiState.update { it.copy(refreshing = false) }
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(it)
                }.onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            refreshing = false,
                            feeds = data,
                        )
                    }
                }
        }
    }

    fun onLoadMore() {
        if (iniJob?.isActive == true) return
        if (refreshJob?.isActive == true) return
        val maxId = uiState.value.feeds.lastOrNull()?.status?.status?.id ?: return
        loadMoreJob?.cancel()
        loadMoreJob = launchInViewModel {
            _uiState.update { it.copy(loadMoreState = LoadState.Loading) }
            loadUserTimeline(maxId)
                .onFailure { t ->
                    _uiState.update { it.copy(loadMoreState = LoadState.Failed(t.toTextStringOrNull())) }
                }.onSuccess { data ->
                    _uiState.update {
                        it.copy(
                            loadMoreState = LoadState.Idle,
                            feeds = it.feeds + data,
                        )
                    }
                }
        }
    }

    private suspend fun loadUserTimeline(maxId: String? = null): Result<List<UserTimelineStatus>> {
        val accountIdResult =
            webFingerBaseUrlToUserIdRepo.getUserId(webFinger, role)
        if (accountIdResult.isFailure) {
            return Result.failure(accountIdResult.exceptionOrNull()!!)
        }
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return fetchStatus(
            accountId = accountIdResult.getOrThrow(),
            maxId = maxId,
        ).map { data ->
            data.filter { it.id != maxId }.map { item -> item.toUiState(platform) }
        }
    }

    private suspend fun fetchStatus(
        accountId: String,
        maxId: String?,
    ): Result<List<ActivityPubStatusEntity>> {
        val showPinned = tabType == UserTimelineTabType.POSTS
        val pinnedStatus = mutableListOf<ActivityPubStatusEntity>()
        val accountRepo = clientManager.getClient(role).accountRepo
        if (showPinned && maxId == null) {
            // first page
            val pinnedStatusResult = accountRepo.getStatuses(
                id = accountId,
                pinned = true,
                excludeReplies = true,
                onlyMedia = false,
            )
            if (pinnedStatusResult.isFailure) {
                return Result.failure(pinnedStatusResult.exceptionOrNull()!!)
            }
            pinnedStatus += pinnedStatusResult.getOrThrow().map { it.copy(pinned = true) }
        }
        return accountRepo.getStatuses(
            id = accountId,
            pinned = false,
            excludeReplies = tabType != UserTimelineTabType.REPLIES,
            onlyMedia = tabType == UserTimelineTabType.MEDIA,
        ).map {
            pinnedStatus + it
        }
    }

    private suspend fun ActivityPubStatusEntity.toUiState(platform: BlogPlatform): UserTimelineStatus {
        val status = statusAdapter.toStatus(this, platform)
        status.preParseRichText()
        val statusUiState = buildStatusUiState(role, status)
        return UserTimelineStatus(
            status = statusUiState,
            pinned = pinned == true,
        )
    }

    private fun updateStatus(status: StatusUiState) {
        _uiState.update { state ->
            val feeds = state.feeds.map {
                if (it.status.status.intrinsicBlog.id == status.status.intrinsicBlog.id) {
                    it.copy(status = status)
                } else {
                    it
                }
            }
            state.copy(feeds = feeds)
        }
    }
}
