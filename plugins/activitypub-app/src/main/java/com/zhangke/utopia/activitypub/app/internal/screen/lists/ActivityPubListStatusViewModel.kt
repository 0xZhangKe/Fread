package com.zhangke.utopia.activitypub.app.internal.screen.lists

import cafe.adriel.voyager.hilt.ScreenModelFactory
import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import dagger.assisted.AssistedFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubListStatusViewModel @Inject constructor(
    private val listStatusRepo: ListStatusRepo,
    private val clientManager: ActivityPubClientManager,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ContainerViewModel<ActivityPubListStatusSubViewModel, ActivityPubListStatusViewModel.Params>() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(listId: String): ActivityPubListStatusViewModel
    }

    override fun createSubViewModel(params: Params) = ActivityPubListStatusSubViewModel(
        listStatusRepo = listStatusRepo,
        clientManager = clientManager,
        buildStatusUiState = buildStatusUiState,
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
