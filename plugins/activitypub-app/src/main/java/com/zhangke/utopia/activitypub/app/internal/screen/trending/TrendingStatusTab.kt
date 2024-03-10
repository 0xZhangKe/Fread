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
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.utopia.activitypub.app.internal.composable.ActivityPubTabNames
import com.zhangke.utopia.activitypub.app.internal.screen.content.ActivityPubListStatusContent

class TrendingStatusTab(private val baseUrl: FormalBaseUrl) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = ActivityPubTabNames.trending
        )

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = getViewModel<TrendingStatusViewModel>().getSubViewModel(baseUrl)
        val uiState by viewModel.uiState.collectAsState()
        ActivityPubListStatusContent(
            uiState = uiState,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onInteractive = viewModel::onInteractive,
            onVoted = viewModel::onVoted,
            nestedScrollConnection = nestedScrollConnection,
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
