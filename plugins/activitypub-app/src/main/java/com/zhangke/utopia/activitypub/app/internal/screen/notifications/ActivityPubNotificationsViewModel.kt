package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.NotificationsRepo
import com.zhangke.utopia.activitypub.app.internal.usecase.status.StatusInteractiveUseCase
import com.zhangke.utopia.activitypub.app.internal.usecase.status.VotePollUseCase
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.common.status.usecase.FormatStatusDisplayTimeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubNotificationsViewModel @Inject constructor(
    private val accountManager: ActivityPubAccountManager,
    private val statusInteractive: StatusInteractiveUseCase,
    private val activityPubStatusAdapter: ActivityPubStatusAdapter,
    private val formatStatusDisplayTime: FormatStatusDisplayTimeUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val notificationsRepo: NotificationsRepo,
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val votePoll: VotePollUseCase,
) : ContainerViewModel<ActivityPubNotificationsSubViewModel, ActivityPubNotificationsViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubNotificationsSubViewModel {
        return ActivityPubNotificationsSubViewModel(
            userUriInsights = params.userUriInsights,
            accountManager = accountManager,
            statusInteractive = statusInteractive,
            activityPubStatusAdapter = activityPubStatusAdapter,
            formatStatusDisplayTime = formatStatusDisplayTime,
            buildStatusUiState = buildStatusUiState,
            notificationsRepo = notificationsRepo,
            clientManager = clientManager,
            accountEntityAdapter = accountEntityAdapter,
            votePoll = votePoll,
        )
    }

    fun getSubViewModel(userUriInsights: UserUriInsights): ActivityPubNotificationsSubViewModel {
        val params = Params(userUriInsights)
        return obtainSubViewModel(params)
    }

    class Params(val userUriInsights: UserUriInsights) : SubViewModelParams() {

        override val key: String
            get() = userUriInsights.toString()
    }
}
