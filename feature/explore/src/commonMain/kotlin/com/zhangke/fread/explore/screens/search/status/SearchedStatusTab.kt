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
import com.zhangke.framework.composable.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.model.StatusUiState
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal class SearchedStatusTab(
    private val locator: PlatformLocator,
    private val query: String,
) : BaseTab() {

    override val options: TabOptions
        @Composable get() = TabOptions(
            title = stringResource(LocalizedString.explorerSearchTabTitleStatus),
        )

    @Composable
    override fun Content() {
        super.Content()
        val backStack = LocalNavBackStack.currentOrThrow
        val viewModel = koinViewModel<SearchStatusViewModel> { parametersOf(locator) }
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
            nestedScrollConnection = null,
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            backStack.add(it)
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
