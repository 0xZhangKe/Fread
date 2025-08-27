package com.zhangke.fread.status.ui.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ObserveScrollStopedPosition(
    listState: LazyListState,
    onPositionChanged: (position: Int) -> Unit,
) {
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { inProgress ->
                if (!inProgress) {
                    val index = listState.firstVisibleItemIndex
                    onPositionChanged(index)
                }
            }
    }
}
