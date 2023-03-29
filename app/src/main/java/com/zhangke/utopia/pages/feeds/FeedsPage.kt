package com.zhangke.utopia.pages.feeds

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.utopia.composable.status.StatusNode

@Composable
fun FeedsPage(
    uiState: FeedsPageUiState,
) {
    LoadableLayout(
        modifier = Modifier.fillMaxSize(),
        state = uiState.feeds
    ) { feeds ->
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(feeds) { item ->
                StatusNode(status = item)
            }
        }
    }
}