package com.zhangke.utopia.activitypub.app.internal.screen.lists

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetStatusInteractionUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubListStatusViewModel @Inject constructor(
    private val platformRepo: ActivityPubPlatformRepo,
    private val listStatusRepo: ListStatusRepo,
    private val getStatusSupportAction: GetStatusInteractionUseCase,
    private val statusInteractive: StatusInteractiveUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val statusAdapter: ActivityPubStatusAdapter,
) : ContainerViewModel<ActivityPubListStatusSubViewModel, ActivityPubListStatusViewModel.Params>() {

    override fun createSubViewModel(params: Params) = ActivityPubListStatusSubViewModel(
        listStatusRepo = listStatusRepo,
        platformRepo = platformRepo,
        getStatusSupportAction = getStatusSupportAction,
        buildStatusUiState = buildStatusUiState,
        statusAdapter = statusAdapter,
        statusInteractive = statusInteractive,
        serverBaseUrl = params.baseUrl,
        listId = params.listId,
    )

    fun getSubViewModel(
        baseUrl: FormalBaseUrl,
        listId: String,
    ): ActivityPubListStatusSubViewModel {
        val params = Params(baseUrl, listId)
        return obtainSubViewModel(params)
    }

    class Params(val baseUrl: FormalBaseUrl, val listId: String) : SubViewModelParams() {
        override val key: String
            get() = "${baseUrl}_$listId"
    }
}
