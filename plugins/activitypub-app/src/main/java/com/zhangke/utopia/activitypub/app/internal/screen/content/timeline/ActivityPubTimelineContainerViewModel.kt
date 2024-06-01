package com.zhangke.utopia.activitypub.app.internal.screen.content.timeline

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.repo.status.ActivityPubTimelineStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.commonbiz.shared.usecase.RefactorToNewBlogUseCase
import com.zhangke.utopia.status.StatusProvider
import com.zhangke.utopia.status.model.IdentityRole

class ActivityPubTimelineContainerViewModel(
    private val statusProvider: StatusProvider,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
    private val refactorToNewBlog: RefactorToNewBlogUseCase,
    private val timelineRepo: ActivityPubTimelineStatusRepo,
) : ContainerViewModel<ActivityPubTimelineViewModel, ActivityPubTimelineContainerViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubTimelineViewModel {
        return ActivityPubTimelineViewModel(
            statusProvider = statusProvider,
            buildStatusUiState = buildStatusUiState,
            refactorToNewBlog = refactorToNewBlog,
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