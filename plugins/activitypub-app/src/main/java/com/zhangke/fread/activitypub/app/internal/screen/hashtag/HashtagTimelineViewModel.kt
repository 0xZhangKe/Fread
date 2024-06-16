package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import android.content.Context
import com.zhangke.activitypub.entities.ActivityPubTagEntity
import com.zhangke.framework.composable.emitTextMessageFromThrowable
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.activitypub.app.R
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class HashtagTimelineViewModel @AssistedInject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusProvider: StatusProvider,
    private val context: Context,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    private val role: IdentityRole,
    private val hashtag: String,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
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
                }.onFailure { e ->
                    mutableErrorMessageFlow.emitTextMessageFromThrowable(e)
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

    private suspend fun loadMore(maxId: String?): Result<List<Status>> {
        return loadHashtagTimeline(maxId)
    }

    private fun buildDescription(hashTag: ActivityPubTagEntity): String {
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
        return context.getString(
            R.string.activity_pub_hashtag_timeline_description,
            posts.toString(),
            participants.toString(),
            todayPosts.toString(),
        )
    }

    private fun getTodayTimeInMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
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

    private fun Result<ActivityPubTagEntity>.handle() {
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

    private suspend fun loadHashtagTimeline(maxId: String? = null): Result<List<Status>> {
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return clientManager.getClient(role)
            .timelinesRepo
            .getTagTimeline(
                hashtag = hashtag,
                limit = StatusConfigurationDefault.config.loadFromServerLimit,
                maxId = maxId,
            ).map { list -> list.map { statusAdapter.toStatus(it, platform) } }
    }
}
