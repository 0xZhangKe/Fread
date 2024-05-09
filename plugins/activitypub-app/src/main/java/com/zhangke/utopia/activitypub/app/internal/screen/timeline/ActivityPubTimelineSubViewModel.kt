package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.status.TimelineStatusRepo
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.utopia.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status

class ActivityPubTimelineSubViewModel(
    private val timelineStatusRepo: TimelineStatusRepo,
    private val statusProvider: StatusProvider,
    buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    private val role: IdentityRole,
    private val type: ActivityPubStatusSourceType,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    init {
        initController(
            coroutineScope = viewModelScope,
            roleResolver = { role },
            loadFirstPageLocalFeeds = ::loadFirstPageLocalFeeds,
            loadNewFromServerFunction = ::loadNewFromServer,
            loadMoreFunction = ::loadMore,
            onStatusUpdate = ::onStatusUpdate,
        )
        initFeeds(true)
        startAutoFetchNewerFeeds()
    }

    private suspend fun loadFirstPageLocalFeeds(): Result<List<Status>> {
        return timelineStatusRepo.getLocalStatus(
            role = role,
            type = type,
        ).let { list ->
            Result.success(list)
        }
    }

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        return timelineStatusRepo.refreshStatus(
            role = role,
            type = type,
        )
    }

    private suspend fun loadMore(maxId: String): Result<List<Status>> {
        return timelineStatusRepo.loadMore(
            role = role,
            type = type,
            maxId = maxId,
        )
    }

    private fun onStatusUpdate(status: Status) {
        launchInViewModel {
            timelineStatusRepo.updateStatus(role, status)
        }
    }
}
