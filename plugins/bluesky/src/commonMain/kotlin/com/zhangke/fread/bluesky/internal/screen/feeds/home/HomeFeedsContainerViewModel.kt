package com.zhangke.fread.bluesky.internal.screen.feeds.home

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.usecase.GetFeedsStatusUseCase
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator

class HomeFeedsContainerViewModel(
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val statusUpdater: StatusUpdater,
    private val statusProvider: StatusProvider,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val getFeedsStatus: GetFeedsStatusUseCase,
) : ContainerViewModel<HomeFeedsViewModel, HomeFeedsContainerViewModel.Params>() {

    fun getViewModel(
        feeds: BlueskyFeeds,
        locator: PlatformLocator,
    ): HomeFeedsViewModel {
        return obtainSubViewModel(
            Params(feeds = feeds, locator = locator)
        )
    }

    override fun createSubViewModel(params: Params): HomeFeedsViewModel {
        return HomeFeedsViewModel(
            statusUiStateAdapter = statusUiStateAdapter,
            statusProvider = statusProvider,
            getFeedsStatus = getFeedsStatus,
            statusUpdater = statusUpdater,
            refactorToNewStatus = refactorToNewStatus,
            feeds = params.feeds,
            locator = params.locator,
        )
    }

    class Params(
        val feeds: BlueskyFeeds,
        val locator: PlatformLocator,
    ) : SubViewModelParams() {

        override val key: String
            get() = feeds.toString() + locator
    }
}
