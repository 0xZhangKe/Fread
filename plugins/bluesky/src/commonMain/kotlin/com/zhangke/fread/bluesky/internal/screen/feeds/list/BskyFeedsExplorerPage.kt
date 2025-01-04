package com.zhangke.fread.bluesky.internal.screen.feeds.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.DefaultFailed
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.fread.bluesky.internal.composable.BlueskyExploringFeeds
import com.zhangke.fread.bluesky.internal.composable.BlueskyFollowingFeeds
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.feeds
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.placeholder.TitleWithAvatarItemPlaceholder
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
        val snackbarState = rememberSnackbarHostState()

        BskyFeedsExplorerContent(
            uiState = uiState,
            snackbarState = snackbarState,
            onBackClick = navigator::pop,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onFollowingFeedsClick = {},
            onAddFeedsClick = viewModel::onAddFeedsClick,
            onSuggestedFeedsClick = {},
        )

        ConsumeSnackbarFlow(snackbarState, viewModel.snackBarMessage)
    }

    @Composable
    private fun BskyFeedsExplorerContent(
        uiState: BskyFeedsExplorerUiState,
        snackbarState: SnackbarHostState,
        onBackClick: () -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onFollowingFeedsClick: (BlueskyFeeds) -> Unit,
        onAddFeedsClick: (BlueskyFeedsUiState) -> Unit,
        onSuggestedFeedsClick: (BlueskyFeeds) -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.feeds),
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarState)
            },
        ) { innerPadding ->
            if (uiState.initializing) {
                InitializingPlaceholder(modifier = Modifier.padding(innerPadding))
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
                                BlueskyFollowingFeeds(
                                    modifier = Modifier.fillMaxSize(),
                                    feeds = it,
                                    onFeedsClick = onFollowingFeedsClick,
                                )
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                )
                            }
                        }
                        if (uiState.suggestedFeeds.isNotEmpty()) {
                            item { Text("Suggested Feeds:") }
                            items(uiState.suggestedFeeds) { item ->
                                BlueskyExploringFeeds(
                                    modifier = Modifier.fillMaxSize(),
                                    feeds = item.feeds,
                                    loading = item.loading,
                                    onAddClick = { onAddFeedsClick(item) },
                                    onFeedsClick = onSuggestedFeedsClick,
                                )
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun InitializingPlaceholder(modifier: Modifier) {
        Column(
            modifier = modifier.fillMaxSize(),
        ) {
            repeat(30) {
                TitleWithAvatarItemPlaceholder(Modifier.fillMaxWidth())
            }
        }
    }
}
