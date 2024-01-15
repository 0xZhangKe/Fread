package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.model.TimelineSourceType
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode

class ActivityPubTimelineScreen(
    private val baseUrl: FormalBaseUrl,
    private val type: TimelineSourceType
) : Screen {

    @Composable
    override fun Content() {
        val viewModel: ActivityPubTimelineViewModel = getViewModel()
        LaunchedEffect(Unit) {
            viewModel.baseUrl = baseUrl
            viewModel.timelineType = type
            viewModel.onPrepared()
        }
        val statusFlow by viewModel.statusFlow.collectAsState()
        val statusList = statusFlow.collectAsLazyPagingItems()
        InlineVideoLazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            itemsIndexed(statusList) { index, status ->
                if (status != null) {
                    FeedsStatusNode(
                        modifier = Modifier.fillMaxWidth(),
                        status = status.status,
                        bottomPanelInteractions = status.bottomInteractions,
                        moreInteractions = status.moreInteractions,
                        onInteractive = viewModel::onInteractive,
                        indexInList = index,
                    )
                }
            }
        }
    }
}
