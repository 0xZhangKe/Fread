package com.zhangke.fread.bluesky.internal.screen.feeds.following

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.DefaultFailed
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.bluesky.bsky_feeds_explorer_more
import com.zhangke.fread.bluesky.internal.composable.BlueskyFollowingFeeds
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.feeds.detail.FeedsDetailScreen
import com.zhangke.fread.bluesky.internal.screen.feeds.explorer.ExplorerFeedsScreen
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.commonbiz.Res
import com.zhangke.fread.commonbiz.feeds
import com.zhangke.fread.status.ui.placeholder.TitleWithAvatarItemPlaceholder
import org.jetbrains.compose.resources.stringResource

class BskyFollowingFeedsPage(private val contentId: String) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel =
            getViewModel<BskyFollowingFeedsViewModel, BskyFollowingFeedsViewModel.Factory> {
                it.create(contentId)
            }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarState = rememberSnackbarHostState()

        BskyFeedsExplorerContent(
            uiState = uiState,
            snackBarState = snackBarState,
            onBackClick = navigator::pop,
            onRefresh = viewModel::onRefresh,
            onFeedsClick = { feed ->
                uiState.role?.let {
                    val screen = FeedsDetailScreen.create(feed, it)
                    screen.onFeedsUpdate = { f ->
                        viewModel.onFeedsUpdate(f)
                    }
                    bottomSheetNavigator.show(screen)
                }
            },
            onExplorerClick = {
                uiState.role?.let { navigator.push(ExplorerFeedsScreen(it)) }
            },
        )

        ConsumeSnackbarFlow(snackBarState, viewModel.snackBarMessage)

        LaunchedEffect(Unit) {
            viewModel.onPageResume()
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun BskyFeedsExplorerContent(
        uiState: BskyFeedsExplorerUiState,
        snackBarState: SnackbarHostState,
        onBackClick: () -> Unit,
        onRefresh: () -> Unit,
        onFeedsClick: (BlueskyFeeds) -> Unit,
        onExplorerClick: () -> Unit = {},
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.feeds),
                    onBackClick = onBackClick,
                    actions = {
                        IconButton(
                            onClick = onExplorerClick,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = stringResource(com.zhangke.fread.bluesky.Res.string.bsky_feeds_explorer_more),
                            )
                        }
                    }
                )
            },
            snackbarHost = {
                SnackbarHost(hostState = snackBarState)
            },
        ) { innerPadding ->
            if (uiState.initializing && uiState.followingFeeds.isEmpty()) {
                InitializingPlaceholder(modifier = Modifier.padding(innerPadding))
            } else {
                val pullRefreshState = rememberPullRefreshState(
                    refreshing = uiState.refreshing,
                    onRefresh = onRefresh,
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(innerPadding)
                        .pullRefresh(pullRefreshState),
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
                            items(uiState.followingFeeds) {
                                BlueskyFollowingFeeds(
                                    modifier = Modifier.fillMaxSize(),
                                    feeds = it,
                                    onFeedsClick = onFeedsClick,
                                )
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                                )
                            }
                            item {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    Button(
                                        modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
                                            .align(Alignment.Center),
                                        onClick = onExplorerClick,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                        ),
                                    ) {
                                        Text(
                                            text = stringResource(com.zhangke.fread.bluesky.Res.string.bsky_feeds_explorer_more)
                                        )
                                    }
                                }
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
