package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.composable.ActivityPubTabNames
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubTimelineType
import com.zhangke.utopia.activitypub.app.internal.screen.content.ActivityPubListStatusContent

class ActivityPubTimelineTab(
    private val baseUrl: FormalBaseUrl,
    private val type: ActivityPubTimelineType
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = when (type) {
                ActivityPubTimelineType.HOME -> ActivityPubTabNames.homeTimeline
                ActivityPubTimelineType.LOCAL -> ActivityPubTabNames.localTimeline
                ActivityPubTimelineType.PUBLIC -> ActivityPubTabNames.publicTimeline
            }
        )

    @Composable
    override fun Screen.TabContent() {
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = getViewModel<ActivityPubTimelineViewModel>().getSubViewModel(baseUrl, type)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubListStatusContent(
            uiState = uiState,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onInteractive = viewModel::onInteractive,
            onVoted = viewModel::onVoted,
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
