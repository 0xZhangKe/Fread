package com.zhangke.utopia.feeds.pages.home.feeds

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getScreenModel
import com.zhangke.framework.composable.TextString
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.utopia.common.status.FeedsConfig
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode

@Composable
fun Screen.FeedsTab(
    feedsConfig: FeedsConfig,
    showSnakeMessage: (TextString) -> Unit,
) {
    val viewModel =
        getScreenModel<FeedsViewModel, FeedsViewModel.Factory>(
            feedsConfig.hashCode().toString()
        ) { factory ->
            factory.create(feedsConfig)
        }
    val uiState by viewModel.state.collectAsState()
    LaunchedEffect(viewModel.errorMessageFlow) {
        viewModel.errorMessageFlow.collect(showSnakeMessage)
    }
    FeedsTabContent(
        uiState = uiState,
        onInteractive = viewModel::onInteractive,
        onRefresh = viewModel::onRefresh,
        onLoadMore = viewModel::onLoadMore,
        onCatchMinFirstVisibleIndex = viewModel::onCatchMinFirstVisibleIndex,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun FeedsTabContent(
    uiState: FeedsScreenUiState,
    onInteractive: (StatusUiInteraction) -> Unit,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onCatchMinFirstVisibleIndex: (Int) -> Unit,
) {
    val state = rememberLoadableInlineVideoLazyColumnState(
        refreshing = uiState.refreshing,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
    )
    val firstVisibleIndex by remember { derivedStateOf { state.lazyListState.firstVisibleItemIndex } }
    var minFirstVisibleIndex = remember {
        Int.MAX_VALUE
    }
    minFirstVisibleIndex = minOf(minFirstVisibleIndex, firstVisibleIndex)
    DisposableEffect(Unit) {

        onDispose {
            onCatchMinFirstVisibleIndex(minFirstVisibleIndex)
        }
    }
    LoadableInlineVideoLazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = state,
        refreshing = uiState.refreshing,
        loading = uiState.loading,
        contentPadding = PaddingValues(
            bottom = 20.dp,
        )
    ) {
        if (uiState.feeds.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        modifier = Modifier
                            .padding(top = 48.dp)
                            .fillMaxWidth(),
                        text = "Empty Placeholder",
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            itemsIndexed(
                items = uiState.feeds,
                key = { _, item ->
                    item.status.id
                },
            ) { index, item ->
                FeedsStatusNode(
                    modifier = Modifier,
                    status = item.status,
                    bottomPanelInteractions = item.bottomInteractions,
                    moreInteractions = item.moreInteractions,
                    onInteractive = onInteractive,
                    indexInList = index,
                )
            }
        }
    }
}
