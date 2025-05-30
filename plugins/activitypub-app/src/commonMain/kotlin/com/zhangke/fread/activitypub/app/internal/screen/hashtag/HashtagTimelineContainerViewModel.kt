package com.zhangke.fread.activitypub.app.internal.screen.hashtag

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.ActivityPubClientManager
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import me.tatarka.inject.annotations.Inject

class HashtagTimelineContainerViewModel @Inject constructor(
    private val clientManager: ActivityPubClientManager,
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val loggedAccountProvider: LoggedAccountProvider,
) : ContainerViewModel<HashtagTimelineViewModel, HashtagTimelineContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): HashtagTimelineViewModel {
        return HashtagTimelineViewModel(
            clientManager = clientManager,
            statusProvider = statusProvider,
            statusUpdater = statusUpdater,
            statusAdapter = statusAdapter,
            platformRepo = platformRepo,
            statusUiStateAdapter = statusUiStateAdapter,
            refactorToNewStatus = refactorToNewStatus,
            role = params.role,
            hashtag = params.tag,
            loggedAccountProvider = loggedAccountProvider,
        )
    }

    fun getViewModel(role: IdentityRole, tag: String): HashtagTimelineViewModel {
        val params = Params(role, tag)
        return obtainSubViewModel(params)
    }

    class Params(
        val role: IdentityRole,
        val tag: String,
    ) : SubViewModelParams() {

        override val key: String
            get() = role.toString() + tag
    }
}