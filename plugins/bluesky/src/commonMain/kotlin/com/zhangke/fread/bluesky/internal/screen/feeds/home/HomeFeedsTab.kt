package com.zhangke.fread.bluesky.internal.screen.feeds.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.model.IdentityRole

class HomeFeedsTab(
    private val feeds: BlueskyFeeds,
    private val role: IdentityRole,
    private val contentCanScrollBackward: MutableState<Boolean>? = null,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(title = feeds.displayName())

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?
    ) {
        val viewModel = screen.getViewModel<HomeFeedsContainerViewModel>().getViewModel(feeds, role)
        val uiState by viewModel.uiState.collectAsState()

        FeedsContent(
            uiState = uiState,
            openScreenFlow = viewModel.openScreenFlow,
            newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            observeScrollToTopEvent = true,
            contentCanScrollBackward = contentCanScrollBackward,
            nestedScrollConnection = nestedScrollConnection,
            onImmersiveEvent = {},
            onScrollInProgress = {},
        )

        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
