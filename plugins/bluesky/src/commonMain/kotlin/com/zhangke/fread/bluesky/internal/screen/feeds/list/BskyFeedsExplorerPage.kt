package com.zhangke.fread.bluesky.internal.screen.feeds.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.DefaultFailed
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.feeds
import com.zhangke.fread.status.model.IdentityRole
import org.jetbrains.compose.resources.stringResource

class BskyFeedsExplorerPage(private val role: IdentityRole) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            getViewModel<BskyFeedsExplorerViewModel, BskyFeedsExplorerViewModel.Factory> {
                it.create(role)
            }
        val uiState by viewModel.uiState.collectAsState()

        BskyFeedsExplorerContent(
            uiState = uiState,
            onBackClick = navigator::pop,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
        )
    }

    @Composable
    private fun BskyFeedsExplorerContent(
        uiState: BskyFeedsExplorerUiState,
        onBackClick: () -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.feeds),
                    onBackClick = onBackClick,
                )
            },
        ) { innerPadding ->
            if (uiState.initializing) {
                InitializingPlaceholder()
            } else {
                val loadableState = rememberLoadableLazyColumnState(
                    refreshing = uiState.refreshing,
                    onRefresh = onRefresh,
                    onLoadMore = onLoadMore,
                )
                LoadableLazyColumn(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    state = loadableState,
                    refreshing = uiState.refreshing,
                    loadState = uiState.loadMoreState,
                ) {
                    if (uiState.pageError != null) {
                        item {
                            Box(modifier = Modifier.fillMaxSize()) {
                                DefaultFailed(
                                    modifier = Modifier.fillMaxSize(),
                                    exception = uiState.pageError,
                                )
                            }
                        }
                    } else {
                        if (uiState.followingFeeds.isNotEmpty()) {
                            item {
                                Text("Following Feeds:")
                            }
                            items(uiState.followingFeeds) {

                            }
                        }
                        if (uiState.suggestedFeeds.isNotEmpty()) {
                            item { Text("Suggested Feeds:") }
                            items(uiState.suggestedFeeds) {

                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InitializingPlaceholder() {

    }
}
