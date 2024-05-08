package com.zhangke.utopia.activitypub.app.internal.screen.notifications

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.ActivityPubAccountManager
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubAccountEntityAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.NotificationsRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.common.status.usecase.FormatStatusDisplayTimeUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubNotificationsViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val accountManager: ActivityPubAccountManager,
    private val formatStatusDisplayTime: FormatStatusDisplayTimeUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val notificationsRepo: NotificationsRepo,
    private val clientManager: ActivityPubClientManager,
    private val accountEntityAdapter: ActivityPubAccountEntityAdapter,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : ContainerViewModel<ActivityPubNotificationsSubViewModel, ActivityPubNotificationsViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubNotificationsSubViewModel {
        return ActivityPubNotificationsSubViewModel(
            statusProvider = statusProvider,
            userUriInsights = params.userUriInsights,
            accountManager = accountManager,
            formatStatusDisplayTime = formatStatusDisplayTime,
            buildStatusUiState = buildStatusUiState,
            notificationsRepo = notificationsRepo,
            clientManager = clientManager,
            accountEntityAdapter = accountEntityAdapter,
            refactorToNewBlog = refactorToNewBlog,
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
