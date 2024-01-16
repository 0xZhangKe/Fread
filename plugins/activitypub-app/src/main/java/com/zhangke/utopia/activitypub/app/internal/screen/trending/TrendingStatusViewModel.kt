package com.zhangke.utopia.activitypub.app.internal.screen.trending

import androidx.lifecycle.ViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendingUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
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
) : ViewModel() {

    private val subViewModelStore = mutableMapOf<String, TrendingStatusSubViewModel>()

    fun getSubViewModel(
        baseUrl: FormalBaseUrl,
    ): TrendingStatusSubViewModel {
        val key = baseUrl.toString()
        return subViewModelStore.getOrPut(key) {
            TrendingStatusSubViewModel(
                getServerTrending = getServerTrending,
                getStatusSupportAction = getStatusSupportAction,
                statusAdapter = statusAdapter,
                buildStatusUiState = buildStatusUiState,
                platformRepo = platformRepo,
                baseUrl = baseUrl,
            )
        }.also { addCloseable(it) }
    }
}
