package com.zhangke.utopia.feeds.pages.home.feeds

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.canScrollBackward
import com.zhangke.framework.composable.textString
import com.zhangke.framework.loadable.lazycolumn.LoadableLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableLazyColumnState
import com.zhangke.utopia.status.ui.StatusNode

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun FeedsPage(
    uiState: FeedsPageUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onShowSnackMessage: suspend (String) -> Unit,
) {
    val feeds = uiState.feedsFlow.collectAsState(initial = emptyList()).value
    val state = rememberLoadableLazyColumnState(
        refreshing = uiState.refreshing,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
    )
    val snackMessage = uiState.snackMessage?.let { textString(it) }
    if (snackMessage.isNullOrEmpty().not()) {
        LaunchedEffect(uiState.snackMessage) {
            onShowSnackMessage(snackMessage!!)
        }
    }
    LoadableLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = state,
        refreshing = uiState.refreshing,
        loading = uiState.loading,
        contentPadding = PaddingValues(
            start = 15.dp,
            top = 15.dp,
            end = 15.dp,
            bottom = 20.dp,
        )
    ) {
        if (feeds.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = "Empty Placeholder")
                }
            }
        } else {
            items(feeds) { item ->
                StatusNode(
                    modifier = Modifier.padding(bottom = 15.dp),
                    status = item,
                )
            }
        }
    }
}
