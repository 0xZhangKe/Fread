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
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.Hashtag
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.hashtag.HashtagUi
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal class SearchedHashtagTab(
    private val locator: PlatformLocator,
    private val query: String
) : BaseTab() {

    override val options: TabOptions
        @Composable get() = TabOptions(
            title = stringResource(LocalizedString.explorerSearchTabTitleHashtag),
        )

    @Composable
    override fun Content() {
        super.Content()
        val backStack = LocalNavBackStack.currentOrThrow
        val viewModel = koinViewModel<SearchHashtagViewModel> { parametersOf(locator) }
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
            nestedScrollConnection = null,
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            backStack.add(it)
        }
        ConsumeSnackbarFlow(
            hostState = snackbarHostState,
            messageTextFlow = viewModel.snackMessageFlow
        )
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
