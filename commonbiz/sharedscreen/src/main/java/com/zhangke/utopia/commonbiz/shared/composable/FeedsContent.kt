package com.zhangke.utopia.commonbiz.shared.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.ConsumeOpenScreenFlow
import com.zhangke.framework.composable.ScrollDirection
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.composable.applyNestedScrollConnection
import com.zhangke.framework.composable.rememberDirectionalLazyListState
import com.zhangke.framework.composable.textString
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.utopia.commonbiz.shared.feeds.CommonFeedsUiState
import com.zhangke.utopia.status.ui.ComposedStatusInteraction
import com.zhangke.utopia.status.ui.StatusListPlaceholder
import com.zhangke.utopia.status.ui.common.LocalMainTabConnection
import com.zhangke.utopia.status.ui.common.NewStatusNotifyBar
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
    nestedScrollConnection: NestedScrollConnection?,
) {
    ConsumeOpenScreenFlow(openScreenFlow)
    val mainTabConnection = LocalMainTabConnection.current
    if (uiState.feeds.isEmpty()) {
        if (uiState.showPagingLoadingPlaceholder) {
            StatusListPlaceholder()
        } else if (uiState.pageErrorContent != null) {
            InitErrorContent(uiState.pageErrorContent)
        }
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLoadableInlineVideoLazyColumnState(
                refreshing = uiState.refreshing,
                onRefresh = onRefresh,
                onLoadMore = onLoadMore,
            )
            val lazyListState = state.lazyListState
            val directional = rememberDirectionalLazyListState(lazyListState).scrollDirection
            LaunchedEffect(directional) {
                if (directional == ScrollDirection.Down) {
                    mainTabConnection.openImmersiveMode()
                } else if (directional == ScrollDirection.Up) {
                    mainTabConnection.closeImmersiveMode()
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
                    bottom = 20.dp,
                )
            ) {
                itemsIndexed(
                    items = uiState.feeds,
                    key = { _, item ->
                        item.status.id
                    },
                ) { index, item ->
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
private fun InitErrorContent(errorMessage: TextString) {
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
