package com.zhangke.utopia.activitypub.app.internal.screen.trending

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
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
            baseUrl = params.baseUrl,
            interactiveHandler = interactiveHandler,
        )
    }

    fun getSubViewModel(
        baseUrl: FormalBaseUrl,
    ): TrendingStatusSubViewModel {
        val params = Params(baseUrl)
        return obtainSubViewModel(params)
    }

    class Params(val baseUrl: FormalBaseUrl) : SubViewModelParams() {
        override val key: String
            get() = baseUrl.toString()
    }
}
