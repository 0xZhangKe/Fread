package com.zhangke.framework.composable

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

@Composable
fun canScrollBackward(state: LazyListState): Boolean {
    val canScrollBackward by remember {
        derivedStateOf {
            state.firstVisibleItemIndex != 0 || state.firstVisibleItemScrollOffset != 0
        }
    }
    return canScrollBackward
}
