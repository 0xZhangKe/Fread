package com.zhangke.framework.composable.collapsable

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun CollapsableTopBarLayout(
    modifier: Modifier = Modifier,
    minTopBarHeight: Dp,
    contentCanScrollBackward: State<Boolean>,
    topBar: @Composable (collapsableProgress: Float) -> Unit,
    scrollableContent: @Composable () -> Unit,
) {
    val density = LocalDensity.current
    val minTopBarHeightPx = with(density) { minTopBarHeight.toPx() }
    var maxTopBarHeightPx: Float? by remember {
        mutableStateOf(null)
    }
    var progress: Float by remember {
        mutableFloatStateOf(0F)
    }
    val connection = rememberCollapsableTopBarLayoutConnection(
        contentCanScrollBackward = contentCanScrollBackward,
        maxPx = maxTopBarHeightPx ?: 0F,
        minPx = minTopBarHeightPx,
    )
    progress = connection.progress

    Column(modifier = modifier.nestedScroll(connection)) {
        Box(
            modifier = Modifier
                .scrollable(rememberScrollState(), Orientation.Vertical)
                .onGloballyPositioned {
                    if (maxTopBarHeightPx == null || maxTopBarHeightPx == 0F) {
                        maxTopBarHeightPx = it.size.height.toFloat()
                    }
                }
        ) {
            topBar(progress)
        }
        Box(
            modifier = Modifier.scrollable(rememberScrollState(), Orientation.Vertical)
        ) {
            scrollableContent()
        }
    }
}
