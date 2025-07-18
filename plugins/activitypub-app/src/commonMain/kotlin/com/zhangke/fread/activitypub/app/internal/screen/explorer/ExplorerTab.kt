package com.zhangke.fread.activitypub.app.internal.screen.explorer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeSnackbarFlow
import com.zhangke.framework.composable.LocalSnackbarHostState
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.textString
import com.zhangke.framework.controller.CommonLoadableUiState
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.utils.pxToDp
import com.zhangke.fread.activitypub.app.Res
import com.zhangke.fread.activitypub.app.activity_pub_explorer_tab_hashtag_title
import com.zhangke.fread.activitypub.app.activity_pub_explorer_tab_status_title
import com.zhangke.fread.activitypub.app.activity_pub_explorer_tab_users_title
import com.zhangke.fread.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.fread.status.model.PlatformLocator
import com.zhangke.fread.status.platform.BlogPlatform
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.RecommendAuthorUi
import com.zhangke.fread.status.ui.StatusListPlaceholder
import com.zhangke.fread.status.ui.common.ObserveScrollInProgressForConnection
import com.zhangke.fread.status.ui.hashtag.HashtagUi
import org.jetbrains.compose.resources.stringResource

class ExplorerTab(
    private val locator: PlatformLocator,
    private val platform: BlogPlatform,
    private val feedsTabType: ExplorerFeedsTabType,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = when (feedsTabType) {
                ExplorerFeedsTabType.STATUS -> stringResource(Res.string.activity_pub_explorer_tab_status_title)
                ExplorerFeedsTabType.USERS -> stringResource(Res.string.activity_pub_explorer_tab_users_title)
                ExplorerFeedsTabType.HASHTAG -> stringResource(Res.string.activity_pub_explorer_tab_hashtag_title)
            }
        )

    @Composable
    override fun TabContent(screen: Screen, nestedScrollConnection: NestedScrollConnection?) {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = screen.getViewModel<ExplorerContainerViewModel>()
            .getViewModel(locator, platform, feedsTabType)
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = LocalSnackbarHostState.current
        ConsumeSnackbarFlow(snackbarHostState, viewModel.errorMessageFlow)
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.push(it)
        }
        ExplorerFeedsTabContent(
            uiState = uiState,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            composedStatusInteraction = viewModel.composedStatusInteraction,
        )
    }

    @Composable
    private fun ExplorerFeedsTabContent(
        uiState: CommonLoadableUiState<ExplorerItem>,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        composedStatusInteraction: ComposedStatusInteraction,
    ) {
        val errorMessage = uiState.errorMessage?.let { textString(it) }
        var containerHeight: Dp? by remember {
            mutableStateOf(null)
        }
        val density = LocalDensity.current
        if (uiState.initializing) {
            StatusListPlaceholder()
        } else {
            val state = rememberLoadableInlineVideoLazyColumnState(
                refreshing = uiState.refreshing,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
            )
            ObserveScrollInProgressForConnection(state.lazyListState)
            LoadableInlineVideoLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        containerHeight = it.size.height.pxToDp(density)
                    },
                state = state,
                refreshing = uiState.refreshing,
                loadState = uiState.loadMoreState,
                contentPadding = PaddingValues(bottom = 64.dp)
            ) {
                itemsIndexed(
                    items = uiState.dataList,
                ) { index, item ->
                    ExplorerItemUi(
                        modifier = Modifier.fillMaxWidth(),
                        item = item,
                        locator = locator,
                        composedStatusInteraction = composedStatusInteraction,
                        indexInList = index,
                    )
                }
                if (!errorMessage.isNullOrEmpty() && uiState.dataList.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .run {
                                    if (containerHeight != null) {
                                        fillMaxWidth().height(containerHeight!!)
                                    } else {
                                        fillMaxSize()
                                    }
                                },
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = errorMessage,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun ExplorerItemUi(
        modifier: Modifier,
        item: ExplorerItem,
        locator: PlatformLocator,
        indexInList: Int,
        composedStatusInteraction: ComposedStatusInteraction,
    ) {
        when (item) {
            is ExplorerItem.ExplorerStatus -> {
                FeedsStatusNode(
                    modifier = modifier,
                    status = item.status,
                    composedStatusInteraction = composedStatusInteraction,
                    indexInList = indexInList,
                )
            }

            is ExplorerItem.ExplorerUser -> {
                RecommendAuthorUi(
                    modifier = modifier,
                    locator = locator,
                    author = item.user,
                    following = item.following,
                    composedStatusInteraction = composedStatusInteraction,
                )
            }

            is ExplorerItem.ExplorerHashtag -> {
                HashtagUi(
                    modifier = modifier,
                    tag = item.hashtag,
                    onClick = {
                        composedStatusInteraction.onHashtagClick(locator, it)
                    },
                )
            }
        }
    }
}
