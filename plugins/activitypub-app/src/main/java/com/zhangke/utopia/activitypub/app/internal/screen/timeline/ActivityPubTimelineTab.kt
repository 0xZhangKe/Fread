package com.zhangke.utopia.activitypub.app.internal.screen.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.utopia.activitypub.app.internal.composable.ActivityPubTabNames
import com.zhangke.utopia.activitypub.app.internal.model.ActivityPubTimelineType
import com.zhangke.utopia.commonbiz.shared.composable.FeedsContent
import com.zhangke.utopia.status.model.IdentityRole

class ActivityPubTimelineTab(
    private val role: IdentityRole,
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
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = getViewModel<ActivityPubTimelineViewModel>().getSubViewModel(role, type)
        val uiState by viewModel.uiState.collectAsState()
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        FeedsContent(
            uiState = uiState,
            openScreenFlow = viewModel.openScreenFlow,
            newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            nestedScrollConnection = nestedScrollConnection,
        )
    }
}
