package com.zhangke.framework.loadable.lazycolumn

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.utils.LoadState

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
    onLoadPrevious: (() -> Unit)? = null,
    loadingContent: (@Composable () -> Unit)? = null,
    content: LazyListScope.() -> Unit,
) {
    val lazyListState = state.lazyListState
    val loadMoreStateInternal by remember(loadState) {
        mutableStateOf(loadState)
    }
    val loadMoreFunction by rememberUpdatedState(newValue = state.loadMoreState.onLoadMore)
    Box(
        modifier = modifier.pullRefresh(state.pullRefreshState)
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
                        LoadMoreUi(
                            loadState = loadMoreStateInternal,
                            onLoadMore = loadMoreFunction,
                        )
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
    ObserveLazyListLoadEvent(
        lazyListState = lazyListState,
        loadMoreRemainCountThreshold = state.loadMoreState.loadMoreRemainCountThreshold,
        loadPreviousPageRemainCountThreshold = 3,
        onLoadPrevious = {
            onLoadPrevious?.invoke()
        },
        onLoadMore = state.loadMoreState.onLoadMore,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberLoadableInlineVideoLazyColumnState(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    refreshThreshold: Dp = PullRefreshDefaults.RefreshThreshold,
    refreshingOffset: Dp = PullRefreshDefaults.RefreshingOffset,
    loadMoreRemainCountThreshold: Int = 3,
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
