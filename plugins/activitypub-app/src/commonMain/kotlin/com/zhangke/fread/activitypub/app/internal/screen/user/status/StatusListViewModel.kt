package com.zhangke.fread.activitypub.app.internal.screen.user.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhangke.activitypub.api.AccountsRepo
import com.zhangke.activitypub.api.PagingResult
import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.richtext.preParseRichText
import com.zhangke.fread.status.status.model.Status
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

class StatusListViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val statusProvider: StatusProvider,
    statusUpdater: StatusUpdater,
    private val platformRepo: ActivityPubPlatformRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    @Assisted private val role: IdentityRole,
    @Assisted val type: StatusListType,
) : ViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    fun interface Factory : ViewModelFactory {

        fun create(role: IdentityRole, type: StatusListType): StatusListViewModel
    }

    private var nextMaxId: String? = null

    init {
        initController(
            coroutineScope = viewModelScope,
            roleResolver = { role },
            loadFirstPageLocalFeeds = {
                Result.success(emptyList())
            },
            loadNewFromServerFunction = ::loadNewDataFromServer,
            loadMoreFunction = { loadMoreDataFromServer() },
            onStatusUpdate = {},
        )
        initFeeds(false)
    }

    private suspend fun loadNewDataFromServer(): Result<RefreshResult> {
        nextMaxId = null
        val accountRepo = clientManager.getClient(role).accountRepo
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return fetchStatuses(accountRepo)
            .map { pagingResult ->
                nextMaxId = pagingResult.pagingInfo.nextMaxId
                RefreshResult(
                    newStatus = pagingResult.data.map { it.toUiState(platform) },
                    deletedStatus = emptyList(),
                )
            }
    }

    private suspend fun loadMoreDataFromServer(): Result<List<Status>> {
        val nextMaxId = nextMaxId
        if (nextMaxId.isNullOrEmpty()) {
            return Result.success(emptyList())
        }
        val accountRepo = clientManager.getClient(role).accountRepo
        val platformResult = platformRepo.getPlatform(role)
        if (platformResult.isFailure) {
            return Result.failure(platformResult.exceptionOrNull()!!)
        }
        val platform = platformResult.getOrThrow()
        return fetchStatuses(accountRepo, nextMaxId)
            .map { pagingResult ->
                this@StatusListViewModel.nextMaxId = pagingResult.pagingInfo.nextMaxId
                pagingResult.data.map { it.toUiState(platform) }
            }
    }

    private suspend fun fetchStatuses(
        accountRepo: AccountsRepo,
        maxId: String? = null,
    ): Result<PagingResult<List<ActivityPubStatusEntity>>> {
        return if (type == StatusListType.FAVOURITES) {
            accountRepo.getFavourites(maxId = maxId)
        } else {
            accountRepo.getBookmarks(maxId = maxId)
        }
    }

    private suspend fun ActivityPubStatusEntity.toUiState(platform: BlogPlatform): StatusUiState {
        val status = statusAdapter.toStatus(this, platform)
        status.preParseRichText()
        return status
    }
}
