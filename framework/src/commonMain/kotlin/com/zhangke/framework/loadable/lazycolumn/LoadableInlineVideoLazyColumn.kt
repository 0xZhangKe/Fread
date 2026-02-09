package com.zhangke.framework.loadable.lazycolumn

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.zhangke.framework.blur.applyBlurSource
import com.zhangke.framework.composable.LocalContentPadding
import com.zhangke.framework.composable.inline.InlineVideoLazyColumn
import com.zhangke.framework.utils.LoadState

@Composable
fun LoadableInlineVideoLazyColumn(
    modifier: Modifier = Modifier,
    state: LoadableLazyInlineVideoColumnState,
    refreshing: Boolean,
    loadState: LoadState,
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
    PullToRefreshBox(
        modifier = modifier,
        state = state.pullRefreshState.pullRefreshState,
        isRefreshing = refreshing,
        onRefresh = { state.pullRefreshState.onRefresh() },
        indicator = {
            PullToRefreshIndicator(
                state = state.pullRefreshState.pullRefreshState,
                refreshing = refreshing,
            )
        },
    ) {
        InlineVideoLazyColumn(
            contentPadding = LocalContentPadding.current,
            state = state.lazyListState,
            modifier = Modifier.applyBlurSource(),
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
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    loadMoreRemainCountThreshold: Int = 3,
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LoadableLazyInlineVideoColumnState {
    val pullRefreshState = rememberPullToRefreshState()
    val pullToRefreshingState = remember(pullRefreshState, onRefresh) {
        PullToRefreshingState(
            pullRefreshState = pullRefreshState,
            onRefresh = onRefresh,
        )
    }

    val lazyListState = rememberLazyListState(
        initialFirstVisibleItemScrollOffset = initialFirstVisibleItemScrollOffset,
        initialFirstVisibleItemIndex = initialFirstVisibleItemIndex,
    )

    val loadMoreState = rememberLoadMoreState(loadMoreRemainCountThreshold, onLoadMore)

    return remember(pullRefreshState, lazyListState, loadMoreState) {
        LoadableLazyInlineVideoColumnState(
            lazyListState = lazyListState,
            pullRefreshState = pullToRefreshingState,
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

data class LoadableLazyInlineVideoColumnState(
    val lazyListState: LazyListState,
    val pullRefreshState: PullToRefreshingState,
    val loadMoreState: LoadMoreState,
)

data class PullToRefreshingState(
    val pullRefreshState: PullToRefreshState,
    val onRefresh: () -> Unit,
)
