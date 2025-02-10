package com.zhangke.fread.activitypub.app.internal.screen.trending

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class TrendingStatusViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val clientManager: ActivityPubClientManager,
    private val statusUpdater: StatusUpdater,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val loggedAccountProvider: LoggedAccountProvider,
) : ContainerViewModel<TrendingStatusSubViewModel, TrendingStatusViewModel.Params>() {

    override fun createSubViewModel(params: Params): TrendingStatusSubViewModel {
        return TrendingStatusSubViewModel(
            statusProvider = statusProvider,
            clientManager = clientManager,
            statusUpdater = statusUpdater,
            statusAdapter = statusAdapter,
            refactorToNewStatus = refactorToNewStatus,
            statusUiStateAdapter = statusUiStateAdapter,
            platformRepo = platformRepo,
            loggedAccountProvider = loggedAccountProvider,
            role = params.role,
        )
    }

    fun getSubViewModel(
        role: IdentityRole,
    ): TrendingStatusSubViewModel {
        val params = Params(role)
        return obtainSubViewModel(params)
    }

    class Params(val role: IdentityRole) : SubViewModelParams() {
        override val key: String
            get() = role.toString()
    }
}
