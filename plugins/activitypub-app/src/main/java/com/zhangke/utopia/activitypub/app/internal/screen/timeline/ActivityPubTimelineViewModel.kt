package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.adapter.ActivityPubStatusAdapter
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubStatusSourceType
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubTimelineType
import com.zhangke.utopia.activitypub.app.internal.repo.platform.ActivityPubPlatformRepo
import com.zhangke.utopia.activitypub.app.internal.repo.status.TimelineStatusRepo
import com.zhangke.utopia.activitypub.app.internal.utils.ActivityPubInteractiveHandler
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubTimelineViewModel @Inject constructor(
    private val interactiveHandler: ActivityPubInteractiveHandler,
    private val timelineStatusRepo: TimelineStatusRepo,
    private val platformRepo: ActivityPubPlatformRepo,
    private val statusAdapter: ActivityPubStatusAdapter,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ContainerViewModel<ActivityPubTimelineSubViewModel, ActivityPubTimelineViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubTimelineSubViewModel {
        return ActivityPubTimelineSubViewModel(
            timelineStatusRepo = timelineStatusRepo,
            platformRepo = platformRepo,
            statusAdapter = statusAdapter,
            buildStatusUiState = buildStatusUiState,
            interactiveHandler = interactiveHandler,
            baseUrl = params.baseUrl,
            type = params.timelineSourceType.toSourceType(),
        )
    }

    fun getSubViewModel(
        baseUrl: FormalBaseUrl,
        timelineSourceType: ActivityPubTimelineType,
    ): ActivityPubTimelineSubViewModel {
        val params = Params(baseUrl, timelineSourceType)
        return obtainSubViewModel(params)
    }

    class Params(
        val baseUrl: FormalBaseUrl,
        val timelineSourceType: ActivityPubTimelineType,
    ) : SubViewModelParams() {
        override val key: String
            get() = "${baseUrl}_${timelineSourceType.name}"
    }

    private fun ActivityPubTimelineType.toSourceType(): ActivityPubStatusSourceType {
        return when (this) {
            ActivityPubTimelineType.HOME -> ActivityPubStatusSourceType.TIMELINE_HOME
            ActivityPubTimelineType.LOCAL -> ActivityPubStatusSourceType.TIMELINE_LOCAL
            ActivityPubTimelineType.PUBLIC -> ActivityPubStatusSourceType.TIMELINE_PUBLIC
        }
    }
}
