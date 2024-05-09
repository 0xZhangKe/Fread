package com.zhangke.utopia.activitypub.app.internal.screen.lists

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.utopia.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status

class ActivityPubListStatusSubViewModel(
    private val statusProvider: StatusProvider,
    private val listStatusRepo: ListStatusRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    refactorToNewBlog: RefactorToNewBlogUseCase,
    private val role: IdentityRole,
    private val listId: String,
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
        return listStatusRepo.getLocalStatus(
            role = role,
            listId = listId,
        ).let { Result.success(it) }
    }

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        return listStatusRepo.refreshStatus(
            role = role,
            type = ActivityPubStatusSourceType.LIST,
            listId = listId,
        )
    }

    private suspend fun loadMore(maxId: String): Result<List<Status>> {
        return listStatusRepo.loadMore(
            role = role,
            type = ActivityPubStatusSourceType.LIST,
            maxId = maxId,
        )
    }

    private fun onStatusUpdate(status: Status) {
        launchInViewModel {
            listStatusRepo.updateStatus(role, status)
        }
    }
}
