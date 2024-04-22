package com.zhangke.utopia.feeds.pages.home.feeds

import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import com.zhangke.utopia.status.ui.feeds.CommonFeedsUiState
import com.zhangke.utopia.status.ui.feeds.FeedsViewModelController
import com.zhangke.utopia.status.ui.feeds.InteractiveHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop

class MixedContentSubViewModel(
    private val contentConfigRepo: ContentConfigRepo,
    private val feedsRepo: FeedsRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
    private val configId: Long,
    interactiveHandler: InteractiveHandler,
) : SubViewModel() {

    private val config = StatusConfigurationDefault.config
    private var mixedContent: ContentConfig.MixedContent? = null

    private val feedsViewModelController = FeedsViewModelController(
        coroutineScope = viewModelScope,
        interactiveHandler = interactiveHandler,
        buildStatusUiState = buildStatusUiState,
        loadFirstPageLocalFeeds = ::loadFirstPageLocalFeeds,
        loadNewFromServerFunction = ::loadNewFromServer,
        loadMoreFunction = ::loadMore,
        resolveRole = ::resolveRole,
        onStatusUpdate = ::onStatusUpdate,
    )

    val uiState: StateFlow<CommonFeedsUiState> get() = feedsViewModelController.uiState
    val errorMessageFlow: SharedFlow<TextString> get() = feedsViewModelController.errorMessageFlow
    val openScreenFlow: SharedFlow<Screen> get() = feedsViewModelController.openScreenFlow
    val newStatusNotifyFlow: SharedFlow<Unit> get() = feedsViewModelController.newStatusNotifyFlow

    private suspend fun loadFirstPageLocalFeeds(): Result<List<Status>> {
        if (mixedContent == null) return Result.success(emptyList())
        return feedsRepo.getLocalFirstPageStatus(
            sourceUriList = mixedContent!!.sourceUriList,
            limit = config.loadFromLocalLimit,
        ).let { Result.success(it) }
    }

    private suspend fun loadNewFromServer(): Result<RefreshResult> {
        if (mixedContent == null) return Result.failure(Exception("mixedContent is null"))
        return feedsRepo.refresh(
            sourceUriList = mixedContent!!.sourceUriList,
            limit = config.loadFromServerLimit,
        )
    }

    private suspend fun loadMore(maxId: String): Result<List<Status>> {
        if (mixedContent == null) return Result.failure(Exception("mixedContent is null"))
        return feedsRepo.getStatus(
            sourceUriList = mixedContent!!.sourceUriList,
            limit = config.loadFromServerLimit,
            maxId = maxId,
        )
    }

    private suspend fun onStatusUpdate(status: Status) {
        feedsRepo.updateStatus(status)
    }

    private fun resolveRole(author: BlogAuthor): IdentityRole {
        return statusProvider.statusSourceResolver
            .resolveRoleByUri(author.uri)
    }

    init {
        launchInViewModel {
            mixedContent = contentConfigRepo.getConfigById(configId) as? ContentConfig.MixedContent
            feedsViewModelController.initFeeds(true)
            feedsViewModelController.startAutoFetchNewerFeeds()
        }
        launchInViewModel {
            statusProvider.accountManager
                .getAllAccountFlow()
                .collect {
                    delay(200)
                    feedsViewModelController.initFeeds(false)
                }
        }
        launchInViewModel {
            feedsRepo.feedsInfoChangedFlow
                .collect {
                    delay(50)
                    feedsViewModelController.initFeeds(false)
                }
        }
        launchInViewModel {
            contentConfigRepo.getConfigFlow(configId)
                .drop(1)
                .collect {
                    delay(50)
                    feedsViewModelController.initFeeds(false)
                }
        }
    }

    fun onRefresh() {
        feedsViewModelController.refresh()
    }

    fun onLoadMore() {
        feedsViewModelController.loadMore()
    }

    fun onInteractive(status: Status, uiInteraction: StatusUiInteraction) {
        feedsViewModelController.onInteractive(status, uiInteraction)
    }

    fun onUserInfoClick(blogAuthor: BlogAuthor) {
        feedsViewModelController.onUserInfoClick(blogAuthor)
    }

    fun onVoted(status: Status, options: List<BlogPoll.Option>) {
        feedsViewModelController.onVoted(status, options)
    }
}
