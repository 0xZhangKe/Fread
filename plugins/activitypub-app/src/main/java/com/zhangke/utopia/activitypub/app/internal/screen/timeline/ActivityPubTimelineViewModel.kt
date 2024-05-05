package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubTimelineType
import com.zhangke.utopia.activitypub.app.internal.repo.status.TimelineStatusRepo
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import com.zhangke.utopia.status.model.IdentityRole
import com.zhangke.utopia.commonbiz.shared.feeds.InteractiveHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubTimelineViewModel @Inject constructor(
    private val interactiveHandler: InteractiveHandler,
    private val timelineStatusRepo: TimelineStatusRepo,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ContainerViewModel<ActivityPubTimelineSubViewModel, ActivityPubTimelineViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubTimelineSubViewModel {
        return ActivityPubTimelineSubViewModel(
            timelineStatusRepo = timelineStatusRepo,
            buildStatusUiState = buildStatusUiState,
            interactiveHandler = interactiveHandler,
            role = params.role,
            type = params.timelineSourceType.toSourceType(),
        )
    }

    fun getSubViewModel(
        role: IdentityRole,
        timelineSourceType: ActivityPubTimelineType,
    ): ActivityPubTimelineSubViewModel {
        val params = Params(role, timelineSourceType)
        return obtainSubViewModel(params)
    }

    class Params(
        val role: IdentityRole,
        val timelineSourceType: ActivityPubTimelineType,
    ) : SubViewModelParams() {
        override val key: String
            get() = "${role}_${timelineSourceType.name}"
    }

    private fun ActivityPubTimelineType.toSourceType(): ActivityPubStatusSourceType {
        return when (this) {
            ActivityPubTimelineType.HOME -> ActivityPubStatusSourceType.TIMELINE_HOME
            ActivityPubTimelineType.LOCAL -> ActivityPubStatusSourceType.TIMELINE_LOCAL
            ActivityPubTimelineType.PUBLIC -> ActivityPubStatusSourceType.TIMELINE_PUBLIC
        }
    }
}
