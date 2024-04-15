package com.zhangke.utopia.activitypub.app.internal.screen.lists

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubListStatusViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val platformRepo: ActivityPubPlatformRepo,
    private val listStatusRepo: ListStatusRepo,
    private val interactiveHandler: ActivityPubInteractiveHandler,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val pollAdapter: ActivityPubPollAdapter,
) : ContainerViewModel<ActivityPubListStatusSubViewModel, ActivityPubListStatusViewModel.Params>() {

    override fun createSubViewModel(params: Params) = ActivityPubListStatusSubViewModel(
        listStatusRepo = listStatusRepo,
        clientManager = clientManager,
        platformRepo = platformRepo,
        buildStatusUiState = buildStatusUiState,
        statusAdapter = statusAdapter,
        interactiveHandler = interactiveHandler,
        pollAdapter = pollAdapter,
        role = params.role,
        listId = params.listId,
    )

    fun getSubViewModel(
        role: IdentityRole,
        listId: String,
    ): ActivityPubListStatusSubViewModel {
        val params = Params(role, listId)
        return obtainSubViewModel(params)
    }

    class Params(val role: IdentityRole, val listId: String) : SubViewModelParams() {
        override val key: String
            get() = "${role}_$listId"
    }
}
