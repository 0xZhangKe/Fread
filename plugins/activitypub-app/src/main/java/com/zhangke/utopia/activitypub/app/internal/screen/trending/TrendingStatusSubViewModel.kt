package com.zhangke.utopia.activitypub.app.internal.screen.trending

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.screen.content.StatusViewModel
import com.zhangke.utopia.activitypub.app.internal.usecase.GetServerTrendingUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase

class TrendingStatusSubViewModel(
    private val getServerTrending: GetServerTrendingUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusInteractive: StatusInteractiveUseCase,
    private val baseUrl: FormalBaseUrl,
) : StatusViewModel(
    platformRepo = platformRepo,
    buildStatusUiState = buildStatusUiState,
    statusAdapter = statusAdapter,
    statusInteractive = statusInteractive,
    serverBaseUrl = baseUrl,
) {

    init {
        prepare()
    }

    override suspend fun getLocalStatus(): List<ActivityPubStatusEntity> {
        return emptyList()
    }

    override suspend fun getRemoteStatus(): Result<List<ActivityPubStatusEntity>> {
        return getServerTrending(baseUrl)
    }

    override suspend fun loadMore(maxId: String): Result<List<ActivityPubStatusEntity>> {
        return getServerTrending(
            baseUrl = baseUrl,
            offset = _uiState.value.status.size,
        )
    }

    override suspend fun updateLocalStatus(status: ActivityPubStatusEntity) {}
}
