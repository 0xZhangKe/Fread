package com.zhangke.fread.activitypub.app.internal.screen.trending

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.activitypub.app.internal.composable.ActivityPubTabNames
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection

internal class TrendingStatusTab(private val locator: PlatformLocator) : BasePagerTab() {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = ActivityPubTabNames.trending
        )

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        super.TabContent(screen, nestedScrollConnection)
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = screen.getViewModel<TrendingStatusViewModel>().getSubViewModel(locator)
        val uiState by viewModel.uiState.collectAsState()
        val mainTabConnection = LocalNestedTabConnection.current
        val coroutineScope = rememberCoroutineScope()
        FeedsContent(
            uiState = uiState,
            openScreenFlow = viewModel.openScreenFlow,
            newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            observeScrollToTopEvent = true,
            nestedScrollConnection = nestedScrollConnection,
            onImmersiveEvent = {
                if (it) {
                    mainTabConnection.openImmersiveMode(coroutineScope)
                } else {
                    mainTabConnection.closeImmersiveMode(coroutineScope)
                }
            },
            onScrollInProgress = {
                mainTabConnection.updateContentScrollInProgress(it)
            },
        )
        LaunchedEffect(mainTabConnection.refreshFlow) {
            mainTabConnection.refreshFlow.collect {
                viewModel.onRefresh()
            }
        }
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
