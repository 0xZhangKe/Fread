package com.zhangke.fread.bluesky.internal.screen.feeds.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreen
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection

class HomeFeedsTab(
    private val feeds: BlueskyFeeds,
    private val locator: PlatformLocator,
    private val contentCanScrollBackward: MutableState<Boolean>? = null,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(title = feeds.displayName())

    @Composable
    override fun TabContent(
        screen: Screen,
        nestedScrollConnection: NestedScrollConnection?
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            screen.getViewModel<HomeFeedsContainerViewModel>().getViewModel(feeds, locator)
        val uiState by viewModel.uiState.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        val mainTabConnection = LocalNestedTabConnection.current
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
            onImmersiveEvent = {
                if (it) {
                    mainTabConnection.openImmersiveMode(coroutineScope)
                } else {
                    mainTabConnection.closeImmersiveMode(coroutineScope)
                }
            },
            onScrollInProgress = {},
            onLoginClick = {
                AddBlueskyContentScreen(baseUrl = locator.baseUrl, loginMode = true)
                    .let { navigator.push(it) }
            },
        )

        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)

        LaunchedEffect(Unit) { viewModel.onPageResume() }
    }
}
