package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import com.zhangke.activitypub.entities.ActivityPubTagEntity
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_hashtag_timeline_description
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.utils.getCurrentInstant
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.getString

class HashtagTimelineViewModel(
    private val clientManager: ActivityPubClientManager,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    private val loggedAccountProvider: LoggedAccountProvider,
    statusUiStateAdapter: StatusUiStateAdapter,
    refactorToNewStatus: RefactorToNewStatusUseCase,
    private val role: IdentityRole,
    private val hashtag: String,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    statusUiStateAdapter = statusUiStateAdapter,
    refactorToNewStatus = refactorToNewStatus,
) {

    private val _hashtagTimelineUiState = MutableStateFlow(
        HashtagTimelineUiState(
            role = role,
            hashTag = hashtag,
            following = false,
            description = "",
        )
    )
    val hashtagTimelineUiState = _hashtagTimelineUiState.asStateFlow()

    init {
        initController(
            coroutineScope = viewModelScope,
            roleResolver = { role },
            loadFirstPageLocalFeeds = { Result.success(emptyList()) },
            loadNewFromServerFunction = ::loadNewFromServer,
            loadMoreFunction = ::loadMore,
            onStatusUpdate = {},
        )
        initFeeds(false)
        launchInViewModel {
            clientManager.getClient(role)
                .accountRepo
                .getTagInformation(hashtag)
                .onSuccess {
                    _hashtagTimelineUiState.value = _hashtagTimelineUiState.value.copy(
                        following = it.following,
                        description = buildDescription(it),
                    )
                }
        }
    }

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        return loadHashtagTimeline().map {
            RefreshResult(
                newStatus = it,
                deletedStatus = emptyList(),
            )
        }
    }

    private suspend fun loadMore(maxId: String?): Result<List<StatusUiState>> {
        return loadHashtagTimeline(maxId)
    }

    private suspend fun buildDescription(hashTag: ActivityPubTagEntity): String {
        val todayTimeInMillis = getTodayTimeInMillis()
        var posts = 0
        var participants = 0
        var todayPosts = 0
        hashTag.history.forEach {
            posts += it.uses
            participants += it.accounts
            if ((it.day * 1000) >= todayTimeInMillis) {
                todayPosts += it.uses
            }
        }
        return getString(
            Res.string.activity_pub_hashtag_timeline_description,
            posts.toString(),
            participants.toString(),
            todayPosts.toString(),
        )
    }

    private fun getTodayTimeInMillis(): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val today = getCurrentInstant().toLocalDateTime(timeZone)
        return LocalDateTime(today.year, today.month, today.dayOfMonth, 0, 0, 0)
            .toInstant(timeZone)
            .toEpochMilliseconds()
    }

    fun onFollowClick() {
        launchInViewModel {
            clientManager.getClient(role)
                .accountRepo
                .followTag(hashtag)
                .handle()
        }
    }

    fun onUnfollowClick() {
        launchInViewModel {
            clientManager.getClient(role)
                .accountRepo
                .unfollowTag(hashtag)
                .handle()
        }
    }

    private suspend fun Result<ActivityPubTagEntity>.handle() {
        this.onSuccess { newEntity ->
            _hashtagTimelineUiState.update { state ->
                state.copy(
                    following = newEntity.following,
                    description = buildDescription(newEntity),
                )
            }
        }.onFailure { e ->
            viewModelScope.launch {
                mutableErrorMessageFlow.emitTextMessageFromThrowable(e)
            }
        }
    }

    private suspend fun loadHashtagTimeline(maxId: String? = null): Result<List<StatusUiState>> {
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        val account = loggedAccountProvider.getAccount(role)
        return clientManager.getClient(role)
            .timelinesRepo
            .getTagTimeline(
                hashtag = hashtag,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                maxId = maxId,
            ).map { list ->
                list.map {
                    statusAdapter.toStatusUiState(
                        entity = it,
                        platform = platform,
                        role = role,
                        loggedAccount = account,
                    )
                }
            }
    }
}
