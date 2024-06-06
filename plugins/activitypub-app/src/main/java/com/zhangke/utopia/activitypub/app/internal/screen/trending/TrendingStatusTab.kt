package com.zhangke.utopia.activitypub.app.internal.screen.trending

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import com.zhangke.utopia.status.ui.common.LocalMainTabConnection

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
        val mainTabConnection = LocalMainTabConnection.current
        val coroutineScope = rememberCoroutineScope()
        FeedsContent(
            uiState = uiState,
            openScreenFlow = viewModel.openScreenFlow,
            newStatusNotifyFlow = viewModel.newStatusNotifyFlow,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            nestedScrollConnection = nestedScrollConnection,
            onImmersiveEvent = {
                if (it) {
                    mainTabConnection.openImmersiveMode(coroutineScope)
                } else {
                    mainTabConnection.closeImmersiveMode(coroutineScope)
                }
            },
        )
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }
}
