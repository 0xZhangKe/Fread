package com.zhangke.utopia.status.ui.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun ObserveMinReadItem(listState: LazyListState, onReadMinIndex: (index: Int) -> Unit) {
    var minIndex by remember {
        mutableIntStateOf(Int.MAX_VALUE)
    }
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }
    if (firstVisibleItemIndex < minIndex && !listState.isScrollInProgress) {
        minIndex = firstVisibleItemIndex
        LaunchedEffect(minIndex) {
            onReadMinIndex(minIndex)
        }
    }
}
