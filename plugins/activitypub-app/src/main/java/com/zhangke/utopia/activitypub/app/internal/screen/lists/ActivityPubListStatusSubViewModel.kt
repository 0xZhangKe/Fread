package com.zhangke.utopia.activitypub.app.internal.screen.lists

import com.zhangke.activitypub.entities.ActivityPubStatusEntity
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.activitypub.app.internal.screen.content.StatusViewModel
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase

class ActivityPubListStatusSubViewModel(
    platformRepo: ActivityPubPlatformRepo,
    private val listStatusRepo: ListStatusRepo,
    buildStatusUiState: BuildStatusUiStateUseCase,
    statusAdapter: ActivityPubStatusAdapter,
    statusInteractive: StatusInteractiveUseCase,
    serverBaseUrl: FormalBaseUrl,
    private val listId: String,
) : StatusViewModel(
    platformRepo = platformRepo,
    buildStatusUiState = buildStatusUiState,
    statusAdapter = statusAdapter,
    statusInteractive = statusInteractive,
    serverBaseUrl = serverBaseUrl,
) {

    init {
        prepare()
    }

    override suspend fun getLocalStatus(): List<ActivityPubStatusEntity> {
        return listStatusRepo.getLocalStatus(
            serverBaseUrl = serverBaseUrl,
            listId = listId,
        )
    }

    override suspend fun getRemoteStatus(): Result<List<ActivityPubStatusEntity>> {
        return listStatusRepo.getRemoteStatus(
            serverBaseUrl = serverBaseUrl,
            listId = listId,
        )
    }

    override suspend fun loadMore(maxId: String): Result<List<ActivityPubStatusEntity>> {
        return listStatusRepo.loadMore(
            serverBaseUrl = serverBaseUrl,
            listId = listId,
            maxId = maxId,
        )
    }

    override suspend fun updateLocalStatus(status: ActivityPubStatusEntity) {
        listStatusRepo.updateEntity(status)
    }
}
