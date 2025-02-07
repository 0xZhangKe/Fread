package com.zhangke.fread.feeds.pages.home.feeds

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.fread.common.content.FreadContentRepo
import com.zhangke.fread.common.feeds.model.RefreshResult
import com.zhangke.fread.common.feeds.repo.FeedsRepo
import com.zhangke.fread.common.status.StatusConfigurationDefault
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.fread.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.feeds.pages.manager.edit.EditMixedContentScreen
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.content.MixedContent
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.status.model.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update

class MixedContentSubViewModel(
    private val contentRepo: FreadContentRepo,
    private val feedsRepo: FeedsRepo,
    statusUpdater: StatusUpdater,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val configId: String,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    statusUpdater = statusUpdater,
    buildStatusUiState = buildStatusUiState,
    refactorToNewBlog = refactorToNewBlog,
) {

    private val _configUiState = MutableStateFlow(MixedContentUiState(null))
    val configUiState = _configUiState.asStateFlow()

    private val config = StatusConfigurationDefault.config
    private var mixedContent: MixedContent? = null

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
            mixedContent = contentRepo.getContent(configId) as? MixedContent
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
            contentRepo.getContentFlow(configId)
                .drop(1)
                .collect {
                    delay(50)
                    mixedContent = it as? MixedContent
                    _configUiState.update { state ->
                        state.copy(config = mixedContent)
                    }
                    initFeeds(false)
                }
        }
    }

    fun onContentTitleClick() {
        val mixedContent = mixedContent ?: return
        launchInViewModel {
            mutableOpenScreenFlow.emit(EditMixedContentScreen(mixedContent.id))
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
        )
    }

    private suspend fun loadMore(maxId: String): Result<List<Status>> {
        if (mixedContent == null) return Result.failure(Exception("mixedContent is null"))
        return feedsRepo.getStatus(
            sourceUriList = mixedContent!!.sourceUriList,
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
