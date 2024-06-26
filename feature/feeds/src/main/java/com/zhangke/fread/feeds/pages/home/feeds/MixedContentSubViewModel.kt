package com.zhangke.fread.feeds.pages.home.feeds

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import com.zhangke.fread.common.routeScreen
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.common.status.repo.ContentConfigRepo
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.ContentConfig
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import com.zhangke.krouter.KRouter
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update

class MixedContentSubViewModel(
    private val contentConfigRepo: ContentConfigRepo,
    private val feedsRepo: FeedsRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val configId: Long,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val _configUiState = MutableStateFlow(MixedContentUiState(null))
    val configUiState = _configUiState.asStateFlow()

    private val config = StatusConfigurationDefault.config
    private var mixedContent: ContentConfig.MixedContent? = null

    init {
        initController(
            coroutineScope = viewModelScope,
            roleResolver = {
                generateRole(it)
            },
            loadFirstPageLocalFeeds = ::loadFirstPageLocalFeeds,
            loadNewFromServerFunction = ::loadNewFromServer,
            loadMoreFunction = ::loadMore,
            onStatusUpdate = ::onStatusUpdate,
        )
        launchInViewModel {
            mixedContent = contentConfigRepo.getConfigById(configId) as? ContentConfig.MixedContent
            _configUiState.update {
                it.copy(config = mixedContent)
            }
            initFeeds(true)
            startAutoFetchNewerFeeds()
        }
        launchInViewModel {
            statusProvider.accountManager
                .getAllAccountFlow()
                .drop(1)
                .collect {
                    delay(200)
                    initFeeds(false)
                }
        }
        launchInViewModel {
            feedsRepo.feedsInfoChangedFlow
                .collect {
                    delay(50)
                    initFeeds(false)
                }
        }
        launchInViewModel {
            contentConfigRepo.getConfigFlow(configId)
                .drop(1)
                .collect {
                    delay(50)
                    mixedContent = it as? ContentConfig.MixedContent
                    _configUiState.update { state ->
                        state.copy(config = mixedContent)
                    }
                    initFeeds(false)
                }
        }
    }

    fun onContentTitleClick() {
        val mixedContent = mixedContent ?: return
        statusProvider.screenProvider
            .getPlatformDetailScreenRoute(mixedContent)
            ?.let { KRouter.routeScreen(it) }
            ?.let { screen ->
                launchInViewModel { mutableOpenScreenFlow.emit(screen) }
            }
    }

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

    private fun generateRole(status: Status): IdentityRole {
        return IdentityRole(status.triggerAuthor.uri, null)
    }
}
