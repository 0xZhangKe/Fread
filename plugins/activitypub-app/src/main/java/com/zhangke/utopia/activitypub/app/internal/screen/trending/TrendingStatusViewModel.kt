package com.zhangke.utopia.activitypub.app.internal.screen.trending

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendingUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrendingStatusViewModel @Inject constructor(
    private val getServerTrending: GetServerTrendingUseCase,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusInteractive: StatusInteractiveUseCase,
) : ContainerViewModel<TrendingStatusSubViewModel, TrendingStatusViewModel.Params>() {

    override fun createSubViewModel(params: Params): TrendingStatusSubViewModel {
        return TrendingStatusSubViewModel(
            getServerTrending = getServerTrending,
            getStatusSupportAction = getStatusSupportAction,
            statusAdapter = statusAdapter,
            buildStatusUiState = buildStatusUiState,
            platformRepo = platformRepo,
            statusInteractive = statusInteractive,
            baseUrl = params.baseUrl,
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
