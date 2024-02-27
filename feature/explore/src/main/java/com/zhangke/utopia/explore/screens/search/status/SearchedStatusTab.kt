package com.zhangke.utopia.explore.screens.search.status

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.utils.LoadState
import com.zhangke.framework.voyager.rootNavigator
import com.zhangke.framework.voyager.tryPush
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.common.status.model.StatusUiState
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.explore.R
import com.zhangke.utopia.status.author.BlogAuthor
import com.zhangke.utopia.status.blog.BlogPoll
import com.zhangke.utopia.status.status.model.Status

class SearchedStatusTab(private val query: String) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(R.string.explorer_search_tab_title_status),
        )

    @Composable
    override fun Screen.TabContent() {
        val navigator = LocalNavigator.currentOrThrow.rootNavigator
        val viewModel = getViewModel<SearchStatusViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(query) {
            viewModel.onRefresh(query)
        }

        SearchStatusTabContent(
            uiState = uiState,
            onUserInfoClick = viewModel::onUserInfoClick,
            onInteractive = viewModel::onInteractive,
            onRefresh = {
                viewModel.onRefresh(query)
            },
            onLoadMore = {
                viewModel.onLoadMore(query)
            },
            onVoted = viewModel::onVoted,
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
        onUserInfoClick: (BlogAuthor) -> Unit,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onVoted: (Status, List<BlogPoll.Option>) -> Unit,
    ) {
        val state = rememberLoadableInlineVideoLazyColumnState(
            refreshing = uiState.refreshing,
            onRefresh = onRefresh,
            onLoadMore = onLoadMore,
        )
        LoadableInlineVideoLazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = state,
            refreshing = uiState.refreshing,
            loading = uiState.loadMoreState == LoadState.Loading,
        ) {
            itemsIndexed(uiState.dataList) { index, item ->
                FeedsStatusNode(
                    modifier = Modifier.fillMaxWidth(),
                    status = item,
                    indexInList = index,
                    onUserInfoClick = onUserInfoClick,
                    onInteractive = onInteractive,
                    onVoted = onVoted,
                )
            }
        }
    }
}
