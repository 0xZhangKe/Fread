package com.zhangke.fread.activitypub.app.internal.screen.trending

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.activitypub.app.internal.composable.ActivityPubTabNames
import com.zhangke.fread.commonbiz.shared.composable.FeedsContent
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import org.koin.compose.viewmodel.koinViewModel

internal class TrendingStatusTab(private val locator: PlatformLocator) : BaseTab() {

    override val options: TabOptions
        @Composable get() = TabOptions(
            title = ActivityPubTabNames.trending
        )

    @Composable
    override fun Content() {
        super.Content()
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = koinViewModel<TrendingStatusViewModel>().getSubViewModel(locator)
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
            nestedScrollConnection = null,
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
