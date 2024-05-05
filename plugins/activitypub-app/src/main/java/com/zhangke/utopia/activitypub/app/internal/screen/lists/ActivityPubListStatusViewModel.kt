package com.zhangke.utopia.activitypub.app.internal.screen.lists

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.internal.repo.status.ListStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubListStatusViewModel @Inject constructor(
    private val listStatusRepo: ListStatusRepo,
    private val interactiveHandler: InteractiveHandler,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ContainerViewModel<ActivityPubListStatusSubViewModel, ActivityPubListStatusViewModel.Params>() {

    override fun createSubViewModel(params: Params) = ActivityPubListStatusSubViewModel(
        listStatusRepo = listStatusRepo,
        buildStatusUiState = buildStatusUiState,
        interactiveHandler = interactiveHandler,
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
