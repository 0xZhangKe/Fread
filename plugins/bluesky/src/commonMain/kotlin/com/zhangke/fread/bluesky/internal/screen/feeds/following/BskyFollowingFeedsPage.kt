package com.zhangke.fread.bluesky.internal.screen.feeds.following

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.DefaultFailed
import com.zhangke.framework.composable.FreadDialog
import com.zhangke.framework.composable.SimpleIconButton
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.noRippleClick
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.fread.bluesky.internal.composable.BlueskyFollowingFeeds
import com.zhangke.fread.bluesky.internal.model.BlueskyFeeds
import com.zhangke.fread.bluesky.internal.screen.feeds.detail.FeedsDetailScreen
import com.zhangke.fread.bluesky.internal.screen.feeds.explorer.ExplorerFeedsScreen
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.localization.LocalizedString
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.ui.placeholder.TitleWithAvatarItemPlaceholder
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import org.jetbrains.compose.resources.stringResource

class BskyFollowingFeedsPage(
    private val contentId: String?,
    private val locator: PlatformLocator?,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val navigator = LocalNavigator.currentOrThrow
        val bottomSheetNavigator = LocalBottomSheetNavigator.current
        val viewModel =
            getViewModel<BskyFollowingFeedsViewModel, BskyFollowingFeedsViewModel.Factory> {
                it.create(contentId, locator)
            }
        val uiState by viewModel.uiState.collectAsState()
        val snackBarState = rememberSnackbarHostState()

        BskyFeedsExplorerContent(
            uiState = uiState,
            snackBarState = snackBarState,
            onBackClick = navigator::pop,
            onRefresh = viewModel::onRefresh,
            onFeedsClick = { feed ->
                uiState.locator?.let {
                    val screen = FeedsDetailScreen.create(feed, it)
                    screen.onFeedsUpdate = { f ->
                        viewModel.onFeedsUpdate(f)
                    }
                    bottomSheetNavigator.show(screen)
                }
            },
            onExplorerClick = {
                uiState.locator?.let { navigator.push(ExplorerFeedsScreen(it)) }
            },
            onFeedsReorder = viewModel::onFeedsOrderChanged,
            onDeleteClick = viewModel::onDeleteClick,
        )

        ConsumeSnackbarFlow(snackBarState, viewModel.snackBarMessage)

        ConsumeFlow(viewModel.finishPageFlow) {
            navigator.pop()
        }

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
        onExplorerClick: () -> Unit,
        onFeedsReorder: (Int, Int) -> Unit,
        onDeleteClick: () -> Unit,
    ) {
        var showDeleteConfirmDialog by remember {
            mutableStateOf(false)
        }
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(LocalizedString.feeds),
                    onBackClick = onBackClick,
                    actions = {
                        SimpleIconButton(
                            onClick = onExplorerClick,
                            imageVector = Icons.Default.Explore,
                            contentDescription = stringResource(LocalizedString.bsky_feeds_explorer_more),
                        )

                        SimpleIconButton(
                            onClick = { showDeleteConfirmDialog = true },
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete content",
                        )
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
                var feedsInUi by remember(uiState.followingFeeds) {
                    mutableStateOf(uiState.followingFeeds)
                }
                key(uiState.followingFeeds) {
                    val state = rememberReorderableLazyListState(
                        onMove = { from, to ->
                            if (feedsInUi.isEmpty()) return@rememberReorderableLazyListState
                            feedsInUi = feedsInUi.toMutableList().apply {
                                if (from.index <= feedsInUi.lastIndex) {
                                    if (to.index > feedsInUi.lastIndex) {
                                        add(removeAt(from.index))
                                    } else {
                                        add(to.index, removeAt(from.index))
                                    }
                                }
                            }
                        },
                        onDragEnd = { startIndex, endIndex ->
                            onFeedsReorder(startIndex, endIndex)
                        },
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                            .padding(innerPadding)
                            .pullRefresh(pullRefreshState)
                            .reorderable(state)
                            .detectReorderAfterLongPress(state),
                        state = state.listState,
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
                            if (feedsInUi.isNotEmpty()) {
                                items(
                                    items = feedsInUi,
                                    key = { it.uiKey },
                                ) { feed ->
                                    ReorderableItem(
                                        state = state,
                                        key = feed.uiKey,
                                    ) { dragging ->
                                        val elevation by animateDpAsState(
                                            targetValue = if (dragging) 16.dp else 0.dp,
                                            label = "BskyPinnedFeedsItemElevation",
                                        )
                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            shadowElevation = elevation,
                                        ) {
                                            BlueskyFollowingFeeds(
                                                modifier = Modifier.fillMaxSize(),
                                                feeds = feed,
                                                onFeedsClick = onFeedsClick,
                                            )
                                        }
                                    }
                                }
                                item {
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        TextButton(
                                            modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
                                                .align(Alignment.Center),
                                            onClick = onExplorerClick,
                                        ) {
                                            Text(
                                                text = stringResource(LocalizedString.bsky_feeds_explorer_more)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (uiState.reordering) {
                Box(
                    modifier = Modifier.fillMaxSize()
                        .noRippleClick { }
                        .background(color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6F)),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                    )
                }
            }
        }
        if (showDeleteConfirmDialog) {
            FreadDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                contentText = stringResource(LocalizedString.statusUiEditContentDeleteDialogContent),
                onNegativeClick = {
                    showDeleteConfirmDialog = false
                },
                onPositiveClick = {
                    showDeleteConfirmDialog = false
                    onDeleteClick()
                },
            )
        }
    }

    private val BlueskyFeeds.uiKey: String get() = "${this::class.simpleName}@${this.id}"

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
