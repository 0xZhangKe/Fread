package com.zhangke.utopia.activitypub.app.internal.screen.lists

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
import com.zhangke.utopia.commonbiz.shared.composable.FeedsContent
import com.zhangke.utopia.status.model.IdentityRole

class ActivityPubListStatusTab(
    private val role: IdentityRole,
    private val listId: String,
    private val listTitle: String,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = listTitle
        )

    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val snackbarHostState = LocalSnackbarHostState.current
        val viewModel = getViewModel<ActivityPubListStatusViewModel>().getSubViewModel(role, listId)
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
