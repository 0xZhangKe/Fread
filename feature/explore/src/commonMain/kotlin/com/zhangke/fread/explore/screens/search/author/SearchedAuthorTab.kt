package com.zhangke.fread.explore.screens.search.author

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
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
import com.zhangke.framework.nav.BaseTab
import com.zhangke.framework.nav.LocalNavBackStack
import com.zhangke.framework.nav.TabOptions
import com.zhangke.fread.common.browser.LocalActivityBrowserLauncher
import com.zhangke.fread.common.browser.launchWebTabInApp
import com.zhangke.fread.common.page.BasePagerTab
import com.zhangke.fread.common.tryPush
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.author.BlogAuthor
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.BlogAuthorUi
import io.ktor.http.parametersOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal class SearchedAuthorTab(
    private val locator: PlatformLocator,
    private val query: String,
) : BaseTab() {

    override val options: TabOptions
        @Composable get() = TabOptions(
            title = stringResource(LocalizedString.explorerSearchTabTitleAuthor),
        )

    @Composable
    override fun Content() {
        super.Content()
        val backStack = LocalNavBackStack.currentOrThrow
        val viewModel = koinViewModel<SearchAuthorViewModel> { parametersOf(locator) }
        val uiState by viewModel.uiState.collectAsState()

        val snackbarHostState = LocalSnackbarHostState.current
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
    private fun SearchedAuthorContent(
        uiState: CommonLoadableUiState<BlogAuthor>,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onUserInfoClick: (BlogAuthor) -> Unit,
        nestedScrollConnection: NestedScrollConnection?,
    ) {
        val browserLauncher = LocalActivityBrowserLauncher.current
        val coroutineScope = rememberCoroutineScope()
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
                    onUrlClick = {
                        browserLauncher.launchWebTabInApp(coroutineScope, it, locator)
                    },
                )
            }
        }
    }
}
