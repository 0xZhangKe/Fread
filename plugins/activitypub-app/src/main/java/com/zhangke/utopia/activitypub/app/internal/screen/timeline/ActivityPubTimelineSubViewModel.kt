package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.TimelineStatusRepo
import com.zhangke.utopia.activitypub.app.internal.screen.content.StatusViewModel
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase

class ActivityPubTimelineSubViewModel(
    private val timelineStatusRepo: TimelineStatusRepo,
    platformRepo: ActivityPubPlatformRepo,
    statusAdapter: ActivityPubStatusAdapter,
    buildStatusUiState: BuildStatusUiStateUseCase,
    statusInteractive: StatusInteractiveUseCase,
    baseUrl: FormalBaseUrl,
    private val type: ActivityPubStatusSourceType,
) : StatusViewModel(
    platformRepo = platformRepo,
    buildStatusUiState = buildStatusUiState,
    statusInteractive = statusInteractive,
    statusAdapter = statusAdapter,
    serverBaseUrl = baseUrl,
) {

    init {
        prepare()
    }

    override suspend fun getLocalStatus(): List<ActivityPubStatusEntity> {
        return timelineStatusRepo.getLocalStatus(
            serverBaseUrl = serverBaseUrl,
            type = type,
        )
    }

    override suspend fun getRemoteStatus(): Result<List<ActivityPubStatusEntity>> {
        return timelineStatusRepo.getRemoteStatus(
            serverBaseUrl = serverBaseUrl,
            type = type,
        )
    }

    override suspend fun loadMore(maxId: String): Result<List<ActivityPubStatusEntity>> {
        return timelineStatusRepo.loadMore(
            serverBaseUrl = serverBaseUrl,
            type = type,
            maxId = maxId,
        )
    }

    override suspend fun updateLocalStatus(status: ActivityPubStatusEntity) {
        timelineStatusRepo.updateEntity(status)
    }
}
