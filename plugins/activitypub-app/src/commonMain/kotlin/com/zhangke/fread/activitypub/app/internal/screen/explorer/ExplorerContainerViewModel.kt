package com.zhangke.fread.activitypub.app.internal.screen.explorer

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubTagAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform

class ExplorerContainerViewModel (
    private val clientManager: ActivityPubClientManager,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val accountAdapter: ActivityPubAccountEntityAdapter,
    private val hashtagAdapter: ActivityPubTagAdapter,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
) : ContainerViewModel<ExplorerViewModel, ExplorerContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ExplorerViewModel {
        return ExplorerViewModel(
            clientManager = clientManager,
            loggedAccountProvider = loggedAccountProvider,
            activityPubStatusAdapter = activityPubStatusAdapter,
            accountAdapter = accountAdapter,
            hashtagAdapter = hashtagAdapter,
            statusProvider = statusProvider,
            statusUpdater = statusUpdater,
            statusUiStateAdapter = statusUiStateAdapter,
            refactorToNewStatus = refactorToNewStatus,
            locator = params.locator,
            platform = params.platform,
            type = params.type,
        )
    }

    fun getViewModel(
        locator: PlatformLocator,
        platform: BlogPlatform,
        type: ExplorerFeedsTabType,
    ): ExplorerViewModel {
        return obtainSubViewModel(Params(locator, platform, type))
    } class Params(
        val locator: PlatformLocator,
        val platform: BlogPlatform,
        val type: ExplorerFeedsTabType,
    ) : SubViewModelParams() {

        override val key: String
            get() = locator.toString() + platform + type
    }
}