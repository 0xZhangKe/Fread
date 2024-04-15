package com.zhangke.utopia.activitypub.app.internal.screen.trending

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrendingStatusViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val interactiveHandler: ActivityPubInteractiveHandler,
    private val pollAdapter: ActivityPubPollAdapter,
) : ContainerViewModel<TrendingStatusSubViewModel, TrendingStatusViewModel.Params>() {

    override fun createSubViewModel(params: Params): TrendingStatusSubViewModel {
        return TrendingStatusSubViewModel(
            clientManager = clientManager,
            statusAdapter = statusAdapter,
            buildStatusUiState = buildStatusUiState,
            platformRepo = platformRepo,
            pollAdapter = pollAdapter,
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
