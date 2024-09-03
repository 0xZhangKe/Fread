package com.zhangke.fread.commonbiz.shared.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.composable.textString
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.fread.commonbiz.shared.feeds.CommonFeedsUiState
import com.zhangke.fread.commonbiz.shared.screen.R
import com.zhangke.fread.status.ui.ComposedStatusInteraction
import com.zhangke.fread.status.ui.StatusListPlaceholder
import com.zhangke.fread.status.ui.common.LocalNestedTabConnection
import com.zhangke.fread.status.ui.common.NewStatusNotifyBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FeedsContent(
    uiState: CommonFeedsUiState,
    openScreenFlow: SharedFlow<Screen>,
    newStatusNotifyFlow: SharedFlow<Unit>,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    composedStatusInteraction: ComposedStatusInteraction,
    nestedScrollConnection: NestedScrollConnection? = null,
    observeScrollToTopEvent: Boolean = false,
    contentCanScrollBackward: MutableState<Boolean>? = null,
    onImmersiveEvent: ((immersive: Boolean) -> Unit)? = null,
    onScrollInProgress: ((Boolean) -> Unit)? = null,
) {
    ConsumeOpenScreenFlow(openScreenFlow)
    if (uiState.feeds.isEmpty()) {
        if (uiState.showPagingLoadingPlaceholder) {
            StatusListPlaceholder()
        } else if (uiState.pageErrorContent != null) {
            InitErrorContent(uiState.pageErrorContent)
        } else {
            EmptyListContent()
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLoadableInlineVideoLazyColumnState(
                refreshing = uiState.refreshing,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
            )
            val lazyListState = state.lazyListState
            if (contentCanScrollBackward != null) {
                val canScrollBackward by remember {
                    derivedStateOf {
                        lazyListState.firstVisibleItemIndex != 0 || lazyListState.firstVisibleItemScrollOffset != 0
                    }
                }
                contentCanScrollBackward.value = canScrollBackward
            }
            if (onImmersiveEvent != null) {
                ObserveForImmersive(
                    listState = lazyListState,
                    onImmersiveEvent = onImmersiveEvent,
                )
            }
            if (onScrollInProgress != null) {
                LaunchedEffect(lazyListState.isScrollInProgress) {
                    onScrollInProgress(lazyListState.isScrollInProgress)
                }
            }
            val feedsConnection = LocalNestedTabConnection.current
            if (observeScrollToTopEvent) {
                LaunchedEffect(feedsConnection, lazyListState) {
                    feedsConnection.scrollToTopFlow.collect {
                        if (lazyListState.layoutInfo.totalItemsCount > 0) {
                            lazyListState.animateScrollToItem(0)
                        }
                    }
                }
            }
            LoadableInlineVideoLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .applyNestedScrollConnection(nestedScrollConnection),
                state = state,
                refreshing = uiState.refreshing,
                loadState = uiState.loadMoreState,
                contentPadding = PaddingValues(
                    bottom = 80.dp,
                ),
            ) {
                itemsIndexed(uiState.feeds) { index, item ->
                    FeedsStatusNode(
                        modifier = Modifier.fillMaxWidth(),
                        status = item,
                        composedStatusInteraction = composedStatusInteraction,
                        indexInList = index,
                    )
                }
            }
            var showNewStatusNotifyBar by remember {
                mutableStateOf(false)
            }
            ConsumeFlow(newStatusNotifyFlow) {
                delay(1000)
                if (state.lazyListState.firstVisibleItemIndex > 0) {
                    showNewStatusNotifyBar = true
                }
                delay(20.seconds)
            }
            val coroutineScope = rememberCoroutineScope()
            AnimatedVisibility(
                modifier = Modifier
                    .padding(top = 32.dp)
                    .align(Alignment.TopCenter),
                visible = showNewStatusNotifyBar,
            ) {
                NewStatusNotifyBar(
                    modifier = Modifier,
                    onClick = {
                        showNewStatusNotifyBar = false
                        coroutineScope.launch {
                            if (uiState.feeds.isNotEmpty()) {
                                state.lazyListState.animateScrollToItem(0)
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
fun InitErrorContent(errorMessage: TextString) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 56.dp, end = 16.dp),
            text = textString(text = errorMessage),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun EmptyListContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 56.dp, end = 16.dp),
            text = stringResource(id = R.string.list_content_empty_placeholder),
            textAlign = TextAlign.Center,
        )
    }
}
