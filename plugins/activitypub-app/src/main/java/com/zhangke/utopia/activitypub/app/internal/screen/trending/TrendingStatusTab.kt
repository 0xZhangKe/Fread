package com.zhangke.utopia.activitypub.app.internal.screen.trending

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
import com.zhangke.utopia.commonbiz.shared.composable.FeedsContent
import com.zhangke.utopia.status.model.IdentityRole

class TrendingStatusTab(private val role: IdentityRole) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = ActivityPubTabNames.trending
        )

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = getViewModel<TrendingStatusViewModel>().getSubViewModel(role)
        val uiState by viewModel.uiState.collectAsState()
        FeedsContent(
            uiState = uiState,
            openScreenFlow = viewModel.openScreenFlow,
            newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
            onInteractive = viewModel::onStatusInteractive,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onUserInfoClick = viewModel::onUserInfoClick,
            onVoted = viewModel::onVoted,
            nestedScrollConnection = nestedScrollConnection,
            onStatusClick = viewModel::onStatusClick,
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
