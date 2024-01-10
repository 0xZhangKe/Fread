package com.zhangke.utopia.activitypub.app.internal.screen.content

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.androidx.AndroidScreen
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType

class ActivityPubTimelineScreen(
    private val configId: Long,
    private val type: TimelineSourceType
) : AndroidScreen() {

    @Composable
    override fun Content() {

    }
}
