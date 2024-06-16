package com.zhangke.fread.explore.screens.search.author

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
import androidx.compose.ui.res.stringResource
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.framework.voyager.tryPush
import com.zhangke.fread.explore.R
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.BlogAuthorUi

class SearchedAuthorTab(
    private val role: IdentityRole,
    private val query: String,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(R.string.explorer_search_tab_title_author),
        )

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent(nestedScrollConnection: NestedScrollConnection?) {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<SearchAuthorViewModel, SearchAuthorViewModel.Factory> {
            it.create(role)
        }
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(query) {
            viewModel.initQuery(query)
        }

        SearchedAuthorContent(
            uiState = uiState,
            onRefresh = {
                viewModel.onRefresh(query)
            },
            onLoadMore = {
                viewModel.onLoadMore(query)
            },
            onUserInfoClick = viewModel::onUserInfoClick,
            nestedScrollConnection = nestedScrollConnection,
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.tryPush(it)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun SearchedAuthorContent(
        uiState: CommonLoadableUiState<BlogAuthor>,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onUserInfoClick: (BlogAuthor) -> Unit,
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
            itemsIndexed(uiState.dataList) { _, item ->
                BlogAuthorUi(
                    modifier = Modifier.fillMaxWidth(),
                    author = item,
                    onClick = onUserInfoClick,
                )
            }
        }
    }
}
