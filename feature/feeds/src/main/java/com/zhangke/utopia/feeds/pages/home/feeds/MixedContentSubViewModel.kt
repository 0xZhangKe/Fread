package com.zhangke.utopia.feeds.pages.home.feeds

import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.lifecycle.SubViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.common.feeds.model.RefreshResult
import com.zhangke.utopia.common.feeds.repo.FeedsRepo
import com.zhangke.utopia.common.status.StatusConfigurationDefault
import com.zhangke.utopia.common.status.repo.ContentConfigRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.feeds.FeedsViewModelController
import com.zhangke.utopia.commonbiz.shared.feeds.IFeedsViewModelController
import com.zhangke.utopia.commonbiz.shared.feeds.IdentityRoleResolver
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.Hashtag
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.status.model.Status
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.drop

class MixedContentSubViewModel(
    private val contentConfigRepo: ContentConfigRepo,
    private val feedsRepo: FeedsRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusProvider: StatusProvider,
    private val configId: Long,
) : SubViewModel(), IFeedsViewModelController by FeedsViewModelController(
    statusProvider = statusProvider,
    buildStatusUiState = buildStatusUiState,
) {

    private val config = StatusConfigurationDefault.config
    private var mixedContent: ContentConfig.MixedContent? = null

    inner class RoleResolver : IdentityRoleResolver {

        override fun resolveRole(blogAuthor: BlogAuthor): IdentityRole {
            return statusProvider.statusSourceResolver
                .resolveRoleByUri(blogAuthor.uri)
        }

        override fun resolveRole(tag: Hashtag): IdentityRole {
            val baseUrl = FormalBaseUrl.parse(tag.url) ?: return IdentityRole.nonIdentityRole
            return IdentityRole(null, baseUrl)
        }
    }

    init {
        initController(
            coroutineScope = viewModelScope,
            roleResolver = RoleResolver(),
            loadFirstPageLocalFeeds = ::loadFirstPageLocalFeeds,
            loadNewFromServerFunction = ::loadNewFromServer,
            loadMoreFunction = ::loadMore,
            onStatusUpdate = ::onStatusUpdate,
        )
        launchInViewModel {
            mixedContent = contentConfigRepo.getConfigById(configId) as? ContentConfig.MixedContent
            initFeeds(true)
            startAutoFetchNewerFeeds()
        }
        launchInViewModel {
            statusProvider.accountManager
                .getAllAccountFlow()
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
                    initFeeds(false)
                }
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
}
