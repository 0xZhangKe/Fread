package com.zhangke.utopia.activitypub.app.internal.screen.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.loadable.lazycolumn.LoadableInlineVideoLazyColumn
import com.zhangke.framework.loadable.lazycolumn.rememberLoadableInlineVideoLazyColumnState
import com.zhangke.framework.utils.LoadState
import com.zhangke.utopia.activitypub.app.internal.composable.ActivityPubStatusUi
import com.zhangke.utopia.common.status.model.StatusUiInteraction
import com.zhangke.utopia.status.status.model.Status

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ActivityPubListStatusContent(
    uiState: FeedsStatusUiState,
    onRefresh: () -> Unit,
    onLoadMore: () -> Unit,
    onInteractive: (Status, StatusUiInteraction) -> Unit,
) {
    val state = rememberLoadableInlineVideoLazyColumnState(
        refreshing = uiState.refreshing,
        onRefresh = onRefresh,
        onLoadMore = onLoadMore,
    )
    Box(modifier = Modifier.fillMaxSize()) {
        LoadableInlineVideoLazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            refreshing = uiState.refreshing,
            loading = uiState.loadMoreState == LoadState.Loading,
            contentPadding = PaddingValues(
                bottom = 20.dp,
            )
        ) {
            itemsIndexed(
                items = uiState.status,
                key = { _, item ->
                    item.status.id
                },
            ) { index, status ->
                ActivityPubStatusUi(
                    modifier = Modifier.fillMaxWidth(),
                    status = status,
                    onInteractive = onInteractive,
                    indexInList = index,
                )
            }
        }
    }
}
