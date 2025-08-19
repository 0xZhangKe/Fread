package com.zhangke.fread.activitypub.app.internal.screen.user.status

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject

class StatusListContainerViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val loggedAccountProvider: LoggedAccountProvider,
) : ContainerViewModel<StatusListViewModel, StatusListContainerViewModel.ViewModelParams>() {

    override fun createSubViewModel(params: ViewModelParams): StatusListViewModel {
        return StatusListViewModel(
            clientManager = clientManager,
            statusAdapter = statusAdapter,
            statusProvider = statusProvider,
            statusUpdater = statusUpdater,
            platformRepo = platformRepo,
            statusUiStateAdapter = statusUiStateAdapter,
            refactorToNewStatus = refactorToNewStatus,
            loggedAccountProvider = loggedAccountProvider,
            locator = params.locator,
            type = params.type,
        )
    }

    fun getViewModel(
        locator: PlatformLocator,
        type: StatusListType,
    ): StatusListViewModel {
        return obtainSubViewModel(ViewModelParams(locator, type))
    }

    class ViewModelParams(
        val locator: PlatformLocator,
        val type: StatusListType,
    ) : SubViewModelParams() {

        override val key: String = locator.toString() + type
    }
}
