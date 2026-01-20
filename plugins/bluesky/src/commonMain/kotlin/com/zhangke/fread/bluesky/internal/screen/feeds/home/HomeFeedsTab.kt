package com.zhangke.fread.bluesky.internal.screen.feeds.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.add.AddBlueskyContentScreenNavKey
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import org.koin.compose.viewmodel.koinViewModel

class HomeFeedsTab(
    private val feeds: BlueskyFeeds,
    private val locator: PlatformLocator,
    private val contentCanScrollBackward: MutableState<Boolean>? = null,
) : BaseTab() {

    override val options: TabOptions
        @Composable get() = TabOptions(title = feeds.displayName())

    @Composable
    override fun Content() {
        val backStack = LocalNavBackStack.currentOrThrow
        val viewModel = koinViewModel<HomeFeedsContainerViewModel>().getViewModel(feeds, locator)
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
            nestedScrollConnection = null,
            onImmersiveEvent = {
                if (it) {
                    mainTabConnection.openImmersiveMode(coroutineScope)
                } else {
                    mainTabConnection.closeImmersiveMode(coroutineScope)
                }
            },
            onScrollInProgress = {},
            onLoginClick = {
                backStack.add(
                    AddBlueskyContentScreenNavKey(
                        baseUrl = locator.baseUrl,
                        loginMode = true,
                    )
                )
            },
        )

        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)

        LaunchedEffect(Unit) { viewModel.onPageResume() }
    }
}
