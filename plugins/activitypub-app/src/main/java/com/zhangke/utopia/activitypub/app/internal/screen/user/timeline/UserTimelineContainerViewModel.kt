package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubPollAdapter
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserTimelineContainerViewModel @Inject constructor(
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    private val interactiveHandler: ActivityPubInteractiveHandler,
    private val pollAdapter: ActivityPubPollAdapter,
) : ContainerViewModel<UserTimelineViewModel, UserTimelineContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): UserTimelineViewModel {
        return UserTimelineViewModel(
            webFingerBaseUrlToUserIdRepo = webFingerBaseUrlToUserIdRepo,
            buildStatusUiState = buildStatusUiState,
            platformRepo = platformRepo,
            statusAdapter = statusAdapter,
            clientManager = clientManager,
            interactiveHandler = interactiveHandler,
            pollAdapter = pollAdapter,
            role = params.role,
            userUriInsights = params.userUriInsights,
        )
    }

    fun getSubViewModel(
        role: IdentityRole,
        userUriInsights: UserUriInsights
    ): UserTimelineViewModel {
        return obtainSubViewModel(
            Params(role, userUriInsights)
        )
    }

    class Params(
        val role: IdentityRole,
        val userUriInsights: UserUriInsights,
    ) : SubViewModelParams() {

        override val key: String
            get() = role.toString() + userUriInsights
    }
}