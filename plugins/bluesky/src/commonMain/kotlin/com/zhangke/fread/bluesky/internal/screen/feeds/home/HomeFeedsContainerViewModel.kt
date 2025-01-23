package com.zhangke.fread.bluesky.internal.screen.feeds.home

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.GetFeedsStatusUseCase
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class HomeFeedsContainerViewModel @Inject constructor(
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusUpdater: StatusUpdater,
    private val statusProvider: StatusProvider,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val getFeedsStatus: GetFeedsStatusUseCase,
) : ContainerViewModel<HomeFeedsViewModel, HomeFeedsContainerViewModel.Params>() {

    fun getViewModel(
        feeds: BlueskyFeeds,
        role: IdentityRole,
    ): HomeFeedsViewModel {
        return obtainSubViewModel(
            Params(feeds = feeds, role = role)
        )
    }

    override fun createSubViewModel(params: Params): HomeFeedsViewModel {
        return HomeFeedsViewModel(
            buildStatusUiState = buildStatusUiState,
            statusProvider = statusProvider,
            getFeedsStatus = getFeedsStatus,
            statusUpdater = statusUpdater,
            refactorToNewBlog = refactorToNewBlog,
            feeds = params.feeds,
            role = params.role,
        )
    }

    class Params(
        val feeds: BlueskyFeeds,
        val role: IdentityRole,
    ) : SubViewModelParams() {

        override val key: String
            get() = feeds.toString() + role
    }
}
