package com.zhangke.fread.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.fread.activitypub.app.ActivityPubAccountManager
import com.zhangke.fread.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubStatusReadStateRepo
import com.zhangke.fread.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.fread.common.status.StatusUpdater
import com.zhangke.fread.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.fread.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.fread.status.StatusProvider
import com.zhangke.fread.status.model.IdentityRole
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubTimelineContainerViewModel @Inject constructor(
    private val statusProvider: StatusProvider,
    private val statusUpdater: StatusUpdater,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val timelineRepo: ActivityPubTimelineStatusRepo,
    private val accountManager: ActivityPubAccountManager,
    private val statusReadStateRepo: ActivityPubStatusReadStateRepo,
) : ContainerViewModel<ActivityPubTimelineViewModel, ActivityPubTimelineContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubTimelineViewModel {
        return ActivityPubTimelineViewModel(
            statusProvider = statusProvider,
            statusUpdater = statusUpdater,
            buildStatusUiState = buildStatusUiState,
            refactorToNewBlog = refactorToNewBlog,
            statusReadStateRepo = statusReadStateRepo,
            accountManager = accountManager,
            timelineRepo = timelineRepo,
            role = params.role,
            type = params.type,
            listId = params.listId,
        )
    }

    fun getSubViewModel(
        role: IdentityRole,
        type: ActivityPubStatusSourceType,
        listId: String?
    ): ActivityPubTimelineViewModel {
        return obtainSubViewModel(
            Params(
                role = role,
                type = type,
                listId = listId,
            )
        )
    }

    class Params(
        val role: IdentityRole,
        val type: ActivityPubStatusSourceType,
        val listId: String?,
    ) : SubViewModelParams() {

        override val key: String
            get() = role.toString() + type + listId
    }
}
