package com.zhangke.framework.loadable.lazycolumn

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.LoadState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadableLazyColumn(
    modifier: Modifier,
    state: LoadableLazyColumnState,
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
    Box(
        modifier = modifier
            .pullRefresh(state.pullRefreshState)
    ) {
        LazyColumn(
            modifier = Modifier,
            state = state.lazyListState,
            contentPadding = contentPadding,
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
                            loadState = loadState,
                            onLoadMore = state.loadMoreState.onLoadMore,
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
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberLoadableLazyColumnState(
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    refreshThreshold: Dp = PullRefreshDefaults.RefreshThreshold,
    refreshingOffset: Dp = PullRefreshDefaults.RefreshingOffset,
    loadMoreRemainCountThreshold: Int = 5,
    lazyListState: LazyListState = rememberLazyListState(),
): LoadableLazyColumnState {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = onRefresh,
        refreshThreshold = refreshThreshold,
        refreshingOffset = refreshingOffset,
    )
    val loadMoreState = rememberLoadMoreState(
        loadMoreRemainCountThreshold = loadMoreRemainCountThreshold,
        onLoadMore = onLoadMore,
    )
    return remember(pullRefreshState, lazyListState, loadMoreState) {
        LoadableLazyColumnState(
            lazyListState = lazyListState,
            pullRefreshState = pullRefreshState,
            loadMoreState = loadMoreState,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
data class LoadableLazyColumnState(
    val lazyListState: LazyListState,
    val pullRefreshState: PullRefreshState,
    val loadMoreState: LoadMoreState,
)
