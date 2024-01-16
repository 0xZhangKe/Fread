package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import androidx.lifecycle.ViewModel
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
) : ViewModel() {

    private val subViewModelStore = mutableMapOf<String, ActivityPubTimeSubViewModel>()

    fun getSubViewModel(
        baseUrl: FormalBaseUrl,
        timelineSourceType: TimelineSourceType,
    ): ActivityPubTimeSubViewModel {
        val key = "${baseUrl}_${timelineSourceType.name}"
        subViewModelStore[key]?.let { return it }
        val subViewModel = ActivityPubTimeSubViewModel(
            getTimelineStatus = getTimelineStatus,
            buildStatusUiState = buildStatusUiState,
            baseUrl = baseUrl,
            timelineType = timelineSourceType,
        )
        addCloseable(subViewModel)
        return subViewModel
    }
}
