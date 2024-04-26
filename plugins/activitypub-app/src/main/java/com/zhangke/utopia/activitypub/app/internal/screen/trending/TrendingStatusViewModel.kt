package com.zhangke.utopia.activitypub.app.internal.screen.trending

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.status.ui.feeds.InteractiveHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrendingStatusViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val interactiveHandler: InteractiveHandler,
) : ContainerViewModel<TrendingStatusSubViewModel, TrendingStatusViewModel.Params>() {

    override fun createSubViewModel(params: Params): TrendingStatusSubViewModel {
        return TrendingStatusSubViewModel(
            clientManager = clientManager,
            statusAdapter = statusAdapter,
            buildStatusUiState = buildStatusUiState,
            platformRepo = platformRepo,
            role = params.role,
            interactiveHandler = interactiveHandler,
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
