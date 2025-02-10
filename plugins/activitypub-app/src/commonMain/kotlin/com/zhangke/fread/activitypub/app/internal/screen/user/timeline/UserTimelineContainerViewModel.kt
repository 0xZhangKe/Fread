package com.zhangke.fread.activitypub.app.internal.screen.user.timeline

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.utils.WebFinger
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.repo.WebFingerBaseUrlToUserIdRepo
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class UserTimelineContainerViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val webFingerBaseUrlToUserIdRepo: WebFingerBaseUrlToUserIdRepo,
    private val statusUpdater: StatusUpdater,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val clientManager: ActivityPubClientManager,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val loggedAccountProvider: LoggedAccountProvider,
) : ContainerViewModel<UserTimelineViewModel, UserTimelineContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): UserTimelineViewModel {
        return UserTimelineViewModel(
            statusProvider = statusProvider,
            statusUpdater = statusUpdater,
            webFingerBaseUrlToUserIdRepo = webFingerBaseUrlToUserIdRepo,
            statusUiStateAdapter = statusUiStateAdapter,
            platformRepo = platformRepo,
            statusEntityAdapter = statusAdapter,
            clientManager = clientManager,
            refactorToNewStatus = refactorToNewStatus,
            tabType = params.tabType,
            role = params.role,
            webFinger = params.webFinger,
            loggedAccountProvider = loggedAccountProvider,
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
