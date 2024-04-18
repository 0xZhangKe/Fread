package com.zhangke.utopia.activitypub.app.internal.screen.user.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights
import com.zhangke.utopia.activitypub.app.internal.screen.content.ActivityPubListStatusContent
import com.zhangke.utopia.status.model.IdentityRole

class UserTimelineTab(
    private val contentCanScrollBackward: MutableState<Boolean>,
    private val role: IdentityRole,
    private val userUriInsights: UserUriInsights,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(R.string.activity_pub_user_detail_tab_timeline)
        )

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val viewModel =
            getViewModel<UserTimelineContainerViewModel>().getSubViewModel(role, userUriInsights)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubListStatusContent(
            uiState = uiState,
            role = role,
            onLoadMore = viewModel::loadMore,
            onRefresh = viewModel::refresh,
            onInteractive = viewModel::onInteractive,
            canScrollBackward = contentCanScrollBackward,
            onVoted = viewModel::onVoted,
            nestedScrollConnection = nestedScrollConnection,
        )
        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
