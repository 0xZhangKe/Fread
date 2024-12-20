package com.zhangke.fread.explore.screens.search.status

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.common.status.model.StatusUiState
import com.zhangke.fread.common.tryPush
import com.zhangke.fread.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.fread.explore.Res
import com.zhangke.fread.explore.explorer_search_tab_title_status
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import org.jetbrains.compose.resources.stringResource

internal class SearchedStatusTab(private val role: IdentityRole, private val query: String) : BasePagerTab() {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(Res.string.explorer_search_tab_title_status),
        )

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        val viewModel = screen.getViewModel<SearchStatusViewModel, SearchStatusViewModel.Factory> {
            it.create(role)
        }
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(query) {
            viewModel.initQuery(query)
        }

        SearchStatusTabContent(
            uiState = uiState,
            composedStatusInteraction = viewModel.composedStatusInteraction,
            onRefresh = {
                viewModel.onRefresh(query)
            },
            onLoadMore = {
                viewModel.onLoadMore(query)
            },
            nestedScrollConnection = nestedScrollConnection,
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.tryPush(it)
        }
        val snackbarHostState = LocalSnackbarHostState.currentOrThrow
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun SearchStatusTabContent(
        uiState: CommonLoadableUiState<StatusUiState>,
        composedStatusInteraction: ComposedStatusInteraction,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        val state = rememberLoadableInlineVideoLazyColumnState(
            refreshing = uiState.refreshing,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
        )
        LoadableInlineVideoLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .applyNestedScrollConnection(nestedScrollConnection),
            state = state,
            refreshing = uiState.refreshing,
            loadState = uiState.loadMoreState,
        ) {
            itemsIndexed(uiState.dataList) { index, item ->
                FeedsStatusNode(
                    modifier = Modifier.fillMaxWidth(),
                    status = item,
                    indexInList = index,
                    composedStatusInteraction = composedStatusInteraction,
                )
            }
        }
    }
}
