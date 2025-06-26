package com.zhangke.fread.explore.screens.search.hashtag

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
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.explore.Res
import com.zhangke.fread.explore.explorer_search_tab_title_hashtag
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.hashtag.HashtagUi
import org.jetbrains.compose.resources.stringResource

internal class SearchedHashtagTab(private val locator: PlatformLocator, private val query: String) : BasePagerTab() {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(Res.string.explorer_search_tab_title_hashtag),
        )

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        super.TabContent(screen, nestedScrollConnection)
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = with(screen) {
            getViewModel<SearchHashtagViewModel, SearchHashtagViewModel.Factory> {
                it.create(locator)
            }
        }
        val uiState by viewModel.uiState.collectAsState()

        val snackbarHostState = LocalSnackbarHostState.current

        LaunchedEffect(query) {
            viewModel.initQuery(query)
        }

        SearchedHashtagContent(
            uiState = uiState,
            onRefresh = {
                viewModel.onRefresh(query)
            },
            onLoadMore = {
                viewModel.onLoadMore(query)
            },
            onHashtagClick = viewModel::onHashtagClick,
            nestedScrollConnection = nestedScrollConnection,
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.push(it)
        }
        ConsumeSnackbarFlow(hostState = snackbarHostState, messageTextFlow = viewModel.snackMessageFlow)
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun SearchedHashtagContent(
        uiState: CommonLoadableUiState<Hashtag>,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onHashtagClick: (Hashtag) -> Unit,
        nestedScrollConnection: NestedScrollConnection?
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
                HashtagUi(
                    modifier = Modifier.fillMaxWidth(),
                    tag = item,
                    onClick = onHashtagClick,
                )
            }
        }
    }
}
