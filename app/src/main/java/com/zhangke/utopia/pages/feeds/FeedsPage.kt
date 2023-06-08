package com.zhangke.utopia.pages.feeds

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.zhangke.framework.composable.LoadableLayout
import com.zhangke.utopia.composable.status.StatusNode

@Composable
fun FeedsPage(
    modifier: Modifier = Modifier,
    uiState: FeedsPageUiState,
) {
    LoadableLayout(
        modifier = modifier,
        state = uiState.feeds,
    ) { feeds ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 15.dp, end = 15.dp, bottom = 20.dp)
        ) {
            items(feeds) { item ->
                StatusNode(
                    modifier = Modifier.padding(bottom = 15.dp),
                    status = item,
                )
            }
        }
    }
}
