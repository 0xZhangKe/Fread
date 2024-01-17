package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import com.zhangke.framework.lifecycle.ContainerViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.activitypub.app.internal.usecase.status.GetTimelineStatusUseCase
import com.zhangke.utopia.common.status.usecase.BuildStatusUiStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ActivityPubTimelineViewModel @Inject constructor(
    private val getTimelineStatus: GetTimelineStatusUseCase,
    private val buildStatusUiState: BuildStatusUiStateUseCase,
) : ContainerViewModel<ActivityPubTimeSubViewModel, ActivityPubTimelineViewModel.Params>() {

    override fun createSubViewModel(params: Params): ActivityPubTimeSubViewModel {
        return ActivityPubTimeSubViewModel(
            getTimelineStatus = getTimelineStatus,
            buildStatusUiState = buildStatusUiState,
            baseUrl = params.baseUrl,
            timelineType = params.timelineSourceType,
        )
    }

    fun getSubViewModel(
        baseUrl: FormalBaseUrl,
        timelineSourceType: TimelineSourceType,
    ): ActivityPubTimeSubViewModel {
        val params = Params(baseUrl, timelineSourceType)
        return obtainSubViewModel(params)
    }

    class Params(
        val baseUrl: FormalBaseUrl,
        val timelineSourceType: TimelineSourceType,
    ) : SubViewModelParams() {
        override val key: String
            get() = "${baseUrl}_${timelineSourceType.name}"
    }
}
