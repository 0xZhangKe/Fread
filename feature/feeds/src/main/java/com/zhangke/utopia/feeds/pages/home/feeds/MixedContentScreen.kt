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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
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
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.voyager.tryPush
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.status.status.model.Status

class MixedContentScreen(private val configId: Long) : PagerTab {

    override val options: PagerTabOptions?
        @Composable get() = null

    @Composable
    override fun Screen.TabContent() {
        val snackbarHostState = LocalSnackbarHostState.current
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<MixedContentViewModel>().getSubViewModel(configId)
        val uiState by viewModel.uiState.collectAsState()
        ConsumeSnackbarFlow(hostState = snackbarHostState, messageTextFlow = viewModel.errorMessageFlow)
        MixedContentUi(
            uiState = uiState,
            onInteractive = viewModel::onInteractive,
            onRefresh = viewModel::onRefresh,
            onLoadMore = viewModel::onLoadMore,
            onCatchMinFirstVisibleIndex = viewModel::onCatchMinFirstVisibleIndex,
        )
        ConsumeFlow(viewModel.openScreenFlow) {
            navigator.tryPush(it)
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun MixedContentUi(
        uiState: MixedContentUiState,
        onInteractive: (Status, StatusUiInteraction) -> Unit,
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
                        modifier = Modifier.fillMaxWidth(),
                        status = item,
                        onInteractive = onInteractive,
                        indexInList = index,
                    )
                }
            }
        }
    }
}
