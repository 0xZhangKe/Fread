package com.zhangke.framework.composable.sensitive

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun SensitiveLazyColumn(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    sensitiveLazyColumnState: MutableState<SensitiveLazyColumnState>? = null,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical =
        if (!reverseLayout) Arrangement.Top else Arrangement.Bottom,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    content: LazyListScope.() -> Unit
) {
    if (sensitiveLazyColumnState != null) {
        val layoutInfo by remember { derivedStateOf { state.layoutInfo } }
        val visibleItemsInfo = layoutInfo.visibleItemsInfo
        if (visibleItemsInfo.isNotEmpty()) {
            val firstItemLayoutInfo = visibleItemsInfo.first()
            val lastItemLayoutInfo = visibleItemsInfo.last()
            val firstVisiblePercent = state.visibilityPercent(firstItemLayoutInfo)
            val lastVisiblePercent = state.visibilityPercent(lastItemLayoutInfo)
            val isScrollInProgress = state.isScrollInProgress
            LaunchedEffect(
                visibleItemsInfo,
                isScrollInProgress,
            ) {
                sensitiveLazyColumnState.value = SensitiveLazyColumnState(
                    firstVisibleIndex = firstItemLayoutInfo.index,
                    firstVisiblePercent = firstVisiblePercent,
                    lastVisibleIndex = lastItemLayoutInfo.index,
                    lastVisiblePercent = lastVisiblePercent,
                    isScrollInProgress = isScrollInProgress,
                )
            }
        }
    }
    LazyColumn(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        reverseLayout = reverseLayout,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        content = content,
    )
}

fun LazyListState.visibilityPercent(info: LazyListItemInfo): Float {
    val cutTop = max(0, layoutInfo.viewportStartOffset - info.offset)
    val cutBottom = max(0, info.offset + info.size - layoutInfo.viewportEndOffset)
    return max(0f, 100f - (cutTop + cutBottom) * 100f / info.size)
}
