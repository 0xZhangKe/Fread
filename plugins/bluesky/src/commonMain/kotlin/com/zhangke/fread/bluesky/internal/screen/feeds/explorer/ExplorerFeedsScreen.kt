package com.zhangke.fread.bluesky.internal.screen.feeds.explorer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.bluesky.bsky_feeds_explorer_more
import com.zhangke.fread.bluesky.internal.composable.BlueskyExploringFeeds
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole
import com.zhangke.fread.status.ui.placeholder.TitleWithAvatarItemPlaceholder
import org.jetbrains.compose.resources.stringResource

class ExplorerFeedsScreen(private val role: IdentityRole) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val viewModel =
            getViewModel<ExplorerFeedsViewModel, ExplorerFeedsViewModel.Factory> {
                it.create(role)
            }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarState = rememberSnackbarHostState()
        ExplorerFeedsContent(
            uiState = uiState,
            snackBarState = snackBarState,
            onBackClick = navigator::pop,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onFeedsClick = {},
            onFollowClick = viewModel::onFollowClick,
        )
        ConsumeSnackbarFlow(snackBarState, viewModel.snackBarMessage)
    }

    @Composable
    private fun ExplorerFeedsContent(
        uiState: ExplorerFeedsUiState,
        snackBarState: SnackbarHostState,
        onBackClick: () -> Unit,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        onFollowClick: (BlueskyFeedsUiState) -> Unit,
        onFeedsClick: (BlueskyFeedsUiState) -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.bsky_feeds_explorer_more),
                    onBackClick = onBackClick,
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarState)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    state = loadableState,
                    loadState = uiState.loadMoreState,
                    refreshing = uiState.refreshing,
                ) {
                    items(uiState.feeds) { item ->
                        BlueskyExploringFeeds(
                            modifier = Modifier.fillMaxSize(),
                            feeds = item.feeds,
                            loading = item.followRequesting,
                            onAddClick = { onFollowClick(item) },
                            onFeedsClick = { onFeedsClick(item) },
                        )
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                        )
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
