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
import com.zhangke.fread.status.model.PlatformLocator
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
            locator = params.locator,
            hashtag = params.tag,
            loggedAccountProvider = loggedAccountProvider,
        )
    }

    fun getViewModel(locator: PlatformLocator, tag: String): HashtagTimelineViewModel {
        val params = Params(locator, tag)
        return obtainSubViewModel(params)
    }

    class Params(
        val locator: PlatformLocator,
        val tag: String,
    ) : SubViewModelParams() {

        override val key: String
            get() = locator.toString() + tag
    }
}