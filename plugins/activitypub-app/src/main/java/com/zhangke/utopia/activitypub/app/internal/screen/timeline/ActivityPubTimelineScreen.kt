package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType

class ActivityPubTimelineScreen(
    private val baseUrl: FormalBaseUrl,
    private val type: TimelineSourceType
) : AndroidScreen() {

    @Composable
    override fun Content() {
        val viewModel: ActivityPubTimelineViewModel = getViewModel()
        LaunchedEffect(Unit) {
            viewModel.baseUrl = baseUrl
            viewModel.timelineType = type
            viewModel.onPrepared()
        }

    }
}
