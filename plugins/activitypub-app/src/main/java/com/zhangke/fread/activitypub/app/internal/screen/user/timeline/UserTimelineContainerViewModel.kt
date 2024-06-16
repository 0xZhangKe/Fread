package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UserTimelineContainerViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
) : ContainerViewModel<UserTimelineViewModel, UserTimelineContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): UserTimelineViewModel {
        return UserTimelineViewModel(
            statusProvider = statusProvider,
            webFingerBaseUrlToUserIdRepo = webFingerBaseUrlToUserIdRepo,
            buildStatusUiState = buildStatusUiState,
            platformRepo = platformRepo,
            statusAdapter = statusAdapter,
            clientManager = clientManager,
            refactorToNewBlog = refactorToNewBlog,
            tabType = params.tabType,
            role = params.role,
            webFinger = params.webFinger,
        )
    }

    fun getSubViewModel(
        tabType: UserTimelineTabType,
        role: IdentityRole,
        webFinger: WebFinger,
    ): UserTimelineViewModel {
        return obtainSubViewModel(
            Params(tabType, role, webFinger)
        )
    }

    class Params(
        val tabType: UserTimelineTabType,
        val role: IdentityRole,
        val webFinger: WebFinger,
    ) : SubViewModelParams() {

        override val key: String
            get() = tabType.toString() + role.toString() + webFinger
    }
}
