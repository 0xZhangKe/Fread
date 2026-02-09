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
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.LoadState

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadableLazyColumn(
    modifier: Modifier,
    state: LoadableLazyColumnState,
    refreshing: Boolean,
    loadState: LoadState,
    lazyColumnModifier: Modifier = Modifier,
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
        LazyColumn(
            modifier = lazyColumnModifier,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberLoadableLazyColumnState(
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    loadMoreRemainCountThreshold: Int = 5,
    lazyListState: LazyListState = rememberLazyListState(),
): LoadableLazyColumnState {
    val pullRefreshState = rememberPullToRefreshState()
    val pullToRefreshingState = remember(pullRefreshState, onRefresh) {
        PullToRefreshingState(
            pullRefreshState = pullRefreshState,
            onRefresh = onRefresh,
        )
    }
    val loadMoreState = rememberLoadMoreState(
        loadMoreRemainCountThreshold = loadMoreRemainCountThreshold,
        onLoadMore = onLoadMore,
    )
    return remember(pullRefreshState, lazyListState, loadMoreState) {
        LoadableLazyColumnState(
            lazyListState = lazyListState,
            pullRefreshState = pullToRefreshingState,
            loadMoreState = loadMoreState,
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
data class LoadableLazyColumnState(
    val lazyListState: LazyListState,
    val pullRefreshState: PullToRefreshingState,
    val loadMoreState: LoadMoreState,
)
