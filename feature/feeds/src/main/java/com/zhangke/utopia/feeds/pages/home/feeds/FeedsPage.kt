package com.zhangke.utopia.feeds.pages.home.feeds

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.textString
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.utopia.commonbiz.shared.composable.FeedsStatusNode
import com.zhangke.utopia.status.status.model.Status

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun FeedsPage(
    uiState: FeedsPageUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onShowSnackMessage: suspend (String) -> Unit,
) {
    val feedsList = rememberSaveable(uiState.feedsFlow) {
        mutableListOf<Status>()
    }
    LaunchedEffect(uiState.feedsFlow) {
        uiState.feedsFlow.collect {
            feedsList.clear()
            feedsList.addAll(it)
        }
    }
    val state = rememberLoadableInlineVideoLazyColumnState(
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
    LoadableInlineVideoLazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = state,
        refreshing = uiState.refreshing,
        loading = uiState.loading,
        contentPadding = PaddingValues(
            bottom = 20.dp,
        )
    ) {
        if (feedsList.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(text = "Empty Placeholder")
                }
            }
        } else {
            itemsIndexed(
                items = feedsList,
                key = { _, item ->
                    item.id
                },
            ) { index, item ->
                FeedsStatusNode(
                    modifier = Modifier,
                    status = item,
                    indexInList = index,
                )
            }
        }
    }
}
