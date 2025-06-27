package com.zhangke.fread.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.fread.activitypub.app.internal.auth.LoggedAccountProvider
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubStatusReadStateRepo
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.fread.common.adapter.StatusUiStateAdapter
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewStatusUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.PlatformLocator
import me.tatarka.inject.annotations.Inject

class ActivityPubTimelineContainerViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val statusUiStateAdapter: StatusUiStateAdapter,
    private val refactorToNewStatus: RefactorToNewStatusUseCase,
    private val loggedAccountProvider: LoggedAccountProvider,
    private val timelineRepo: ActivityPubTimelineStatusRepo,
    private val accountManager: ActivityPubAccountManager,
    private val statusReadStateRepo: ActivityPubStatusReadStateRepo,
) : ContainerViewModel<ActivityPubTimelineViewModel, ActivityPubTimelineContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubTimelineViewModel {
        return ActivityPubTimelineViewModel(
            statusProvider = statusProvider,
            statusUpdater = statusUpdater,
            statusUiStateAdapter = statusUiStateAdapter,
            statusAdapter = statusAdapter,
            loggedAccountProvider = loggedAccountProvider,
            refactorToNewStatus = refactorToNewStatus,
            statusReadStateRepo = statusReadStateRepo,
            accountManager = accountManager,
            timelineRepo = timelineRepo,
            locator = params.locator,
            type = params.type,
            listId = params.listId,
        )
    }

    fun getSubViewModel(
        locator: PlatformLocator,
        type: ActivityPubStatusSourceType,
        listId: String?
    ): ActivityPubTimelineViewModel {
        return obtainSubViewModel(
            Params(
                locator = locator,
                type = type,
                listId = listId,
            )
        )
    }

    class Params(
        val locator: PlatformLocator,
        val type: ActivityPubStatusSourceType,
        val listId: String?,
    ) : SubViewModelParams() {

        override val key: String
            get() = locator.toString() + type + listId
    }
}
