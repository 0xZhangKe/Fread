package com.zhangke.framework.loadable.lazycolumn

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.composable.textString
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.framework.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadableInlineVideoLazyColumn(
    modifier: Modifier = Modifier,
    state: LoadableLazyInlineVideoColumnState,
    refreshing: Boolean,
    loadState: LoadState,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    loadingContent: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    val lazyListState = state.lazyListState
    val listLayoutInfo by remember { derivedStateOf { lazyListState.layoutInfo } }
    Box(
        modifier = modifier
            .pullRefresh(state.pullRefreshState)
    ) {
        InlineVideoLazyColumn(
            contentPadding = contentPadding,
            state = state.lazyListState,
            reverseLayout = reverseLayout,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            flingBehavior = flingBehavior,
            userScrollEnabled = userScrollEnabled,
            content = {
                content()
                item {
                    if (loadingContent != null) {
                        loadingContent()
                    } else {
                        when (loadState) {
                            is LoadState.Loading -> {
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(30.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                            }

                            is LoadState.Failed -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                ) {
                                    var errorMessage = loadState.message?.let { textString(it) }
                                    if (errorMessage.isNullOrEmpty()) {
                                        errorMessage = stringResource(R.string.load_more_error)
                                    }
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = errorMessage,
                                        textAlign = TextAlign.Center,
                                    )
                                    TextButton(
                                        modifier = Modifier.padding(top = 6.dp),
                                        onClick = state.loadMoreState.onLoadMore,
                                    ) {
                                        Text(text = stringResource(R.string.retry))
                                    }
                                }
                            }
                            else -> {}
                        }
                    }
                }
            },
        )
        PullRefreshIndicator(
            modifier = Modifier.align(Alignment.TopCenter),
            refreshing = refreshing,
            state = state.pullRefreshState,
            scale = true,
        )
    }
    val currentLastVisibleIndex = listLayoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
    var inLoadingMoreZone by remember {
        mutableStateOf(false)
    }
    val remainCount = listLayoutInfo.totalItemsCount - currentLastVisibleIndex - 1
    inLoadingMoreZone = listLayoutInfo.totalItemsCount > 0 &&
            remainCount <= state.loadMoreState.loadMoreRemainCountThreshold &&
            listLayoutInfo.totalItemsCount > state.loadMoreState.loadMoreRemainCountThreshold
    if (inLoadingMoreZone) {
        LaunchedEffect(Unit) {
            state.loadMoreState.onLoadMore()
        }
    }
}

@Composable
@ExperimentalMaterialApi
fun rememberLoadableInlineVideoLazyColumnState(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    refreshThreshold: Dp = PullRefreshDefaults.RefreshThreshold,
    refreshingOffset: Dp = PullRefreshDefaults.RefreshingOffset,
    loadMoreRemainCountThreshold: Int = 5,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LoadableLazyInlineVideoColumnState {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = onRefresh,
        refreshingOffset = refreshingOffset,
        refreshThreshold = refreshThreshold,
    )

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset,
        initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
    )

    val loadMoreState = rememberLoadMoreState(loadMoreRemainCountThreshold, onLoadMore)

    return remember(pullRefreshState, lazyListState, loadMoreState) {
        LoadableLazyInlineVideoColumnState(
            lazyListState = lazyListState,
            pullRefreshState = pullRefreshState,
            loadMoreState = loadMoreState,
        )
    }
}

@Composable
fun rememberLoadMoreState(
    loadMoreRemainCountThreshold: Int,
    onLoadMore: () -> Unit,
): LoadMoreState {
    return remember {
        LoadMoreState(loadMoreRemainCountThreshold, onLoadMore)
    }
}

data class LoadMoreState(
    val loadMoreRemainCountThreshold: Int,
    val onLoadMore: () -> Unit,
)

@OptIn(ExperimentalMaterialApi::class)
data class LoadableLazyInlineVideoColumnState(
    val lazyListState: LazyListState,
    val pullRefreshState: PullRefreshState,
    val loadMoreState: LoadMoreState,
)
