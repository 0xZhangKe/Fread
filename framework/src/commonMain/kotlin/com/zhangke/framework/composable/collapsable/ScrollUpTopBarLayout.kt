package com.zhangke.framework.composable.collapsable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity

@Composable
fun ScrollUpTopBarLayout(
    modifier: Modifier = Modifier,
    topBarContent: @Composable BoxScope.(progress: Float) -> Unit,
    headerContent: @Composable BoxScope.(progress: Float) -> Unit,
    contentCanScrollBackward: State<Boolean>,
    /**
     * true: headerContent will immersive behind the top bar.
     * false: headerContent will below the top bar.
     */
    immersiveToTopBar: Boolean = true,
    /**
     * PaddingValues.top is the current visible height occupied by top bar + header area.
     */
    scrollableContent: @Composable BoxScope.(PaddingValues, Float) -> Unit,
) {
    var topBarHeightPx: Int by rememberSaveable { mutableIntStateOf(0) }
    var headerContentHeightPx: Int by rememberSaveable { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val nestedScrollConnection = if (immersiveToTopBar) {
        rememberCollapsableTopBarLayoutConnection(
            contentCanScrollBackward = contentCanScrollBackward,
            maxPx = headerContentHeightPx.toFloat(),
            minPx = topBarHeightPx.toFloat(),
        )
    } else {
        rememberCollapsableTopBarLayoutConnection(
            contentCanScrollBackward = contentCanScrollBackward,
            maxPx = headerContentHeightPx.toFloat(),
            minPx = 0F,
        )
    }
    val progress by rememberUpdatedState(newValue = nestedScrollConnection.progress)
    val topPaddingPx = calculateScrollableTopPadding(
        topBarHeightPx = topBarHeightPx,
        headerContentHeightPx = headerContentHeightPx,
        progress = progress,
        immersiveToTopBar = immersiveToTopBar,
    )
    val scrollableContentPadding = with(density) {
        PaddingValues(top = topPaddingPx.toDp())
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
    ) {
        Layout(
            modifier = Modifier,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    topBarContent(progress)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    headerContent(progress)
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    scrollableContent(scrollableContentPadding, progress)
                }
            },
            measurePolicy = { measurables, constraints ->
                val topBarPlaceable = measurables.first().measure(constraints)
                val headerContentPlaceable = measurables[1].measure(
                    constraints.copy(maxHeight = constraints.maxHeight * 6)
                )
                val scrollableContentPlaceable = measurables[2].measure(constraints)
                if (topBarHeightPx != topBarPlaceable.measuredHeight) {
                    topBarHeightPx = topBarPlaceable.measuredHeight
                }
                if (headerContentHeightPx != headerContentPlaceable.measuredHeight) {
                    headerContentHeightPx = headerContentPlaceable.measuredHeight
                }
                layout(constraints.maxWidth, constraints.maxHeight) {
                    val totalScrollOffset = if (immersiveToTopBar) {
                        headerContentHeightPx - topBarHeightPx
                    } else {
                        headerContentHeightPx
                    }
                    val progressOffset = totalScrollOffset * progress
                    val headerYOffset = if (immersiveToTopBar) {
                        -((progressOffset).coerceAtLeast(0F))
                    } else {
                        topBarHeightPx - ((progressOffset).coerceAtLeast(0F))
                    }
                    scrollableContentPlaceable.placeRelative(0, 0)
                    headerContentPlaceable.placeRelative(0, headerYOffset.toInt())
                    topBarPlaceable.placeRelative(0, 0)
                }
            },
        )
    }
}

private fun calculateScrollableTopPadding(
    topBarHeightPx: Int,
    headerContentHeightPx: Int,
    progress: Float,
    immersiveToTopBar: Boolean,
): Float {
    val headerYOffset = if (immersiveToTopBar) {
        val totalScrollOffset = (headerContentHeightPx - topBarHeightPx).coerceAtLeast(0)
        -(totalScrollOffset * progress)
    } else {
        topBarHeightPx - (headerContentHeightPx * progress)
    }
    return (headerContentHeightPx + headerYOffset).coerceAtLeast(0F)
}
