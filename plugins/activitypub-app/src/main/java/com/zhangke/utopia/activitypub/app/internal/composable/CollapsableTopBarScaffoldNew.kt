package com.zhangke.utopia.activitypub.app.internal.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity

@Composable
fun ScrollUpTopBarLayout(
    modifier: Modifier = Modifier,
    topBarContent: @Composable BoxScope.(progress: Float) -> Unit,
    headerContent: @Composable BoxScope.(progress: Float) -> Unit,
    contentCanScrollBackward: State<Boolean>,
    scrollableContent: @Composable BoxScope.() -> Unit,
) {
    var minTopBarHeightPx: Int by remember {
        mutableIntStateOf(0)
    }
    var maxTopBarHeightPx: Int by remember {
        mutableIntStateOf(0)
    }
    val nestedScrollConnection = rememberCollapsableTopBarLayoutConnection(
        contentCanScrollBackward = contentCanScrollBackward,
        maxPx = maxTopBarHeightPx.toFloat(),
        minPx = minTopBarHeightPx.toFloat(),
    )

    val progress by rememberUpdatedState(newValue = nestedScrollConnection.progress)
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
                    scrollableContent()
                }
            },
            measurePolicy = { measurables, constraints ->
                val topBarPlaceable = measurables.first().measure(constraints)
                val headerContentPlaceable = measurables[1].measure(constraints)
                val scrollableContentPlaceable = measurables[2].measure(constraints)
                if (minTopBarHeightPx != topBarPlaceable.measuredHeight) {
                    minTopBarHeightPx = topBarPlaceable.measuredHeight
                }
                if (maxTopBarHeightPx != headerContentPlaceable.measuredHeight) {
                    maxTopBarHeightPx = headerContentPlaceable.measuredHeight
                }
                layout(constraints.maxWidth, constraints.maxHeight) {
                    val totalHeaderHeight = maxTopBarHeightPx - minTopBarHeightPx
                    val progressOffset = totalHeaderHeight * progress
                    val topBarYOffset = -((progressOffset).coerceAtLeast(0F))
                    headerContentPlaceable.placeRelative(0, topBarYOffset.toInt())
                    scrollableContentPlaceable.placeRelative(
                        x = 0,
                        y = maxTopBarHeightPx - progressOffset.toInt()
                    )
                    topBarPlaceable.placeRelative(0, 0)
                }
            },
        )
    }
}

@Composable
private fun rememberCollapsableTopBarLayoutConnection(
    contentCanScrollBackward: State<Boolean>,
    maxPx: Float,
    minPx: Float,
): ICollapsableTopBarLayoutConnection {
    return if (maxPx <= 0F) {
        remember {
            StaticTopBarLayoutConnection()
        }
    } else {
        remember(contentCanScrollBackward, maxPx, minPx) {
            CollapsableTopBarLayoutConnection(contentCanScrollBackward, maxPx, minPx)
        }
    }
}

interface ICollapsableTopBarLayoutConnection : NestedScrollConnection {

    val progress: Float
}

class StaticTopBarLayoutConnection : NestedScrollConnection, ICollapsableTopBarLayoutConnection {

    override val progress: Float = 0F
}

private class CollapsableTopBarLayoutConnection(
    private val contentCanScrollBackward: State<Boolean>,
    private val maxPx: Float,
    private val minPx: Float,
) : NestedScrollConnection, ICollapsableTopBarLayoutConnection {

    private var topBarHeight: Float = maxPx
        set(value) {
            field = value
            progress = 1 - (topBarHeight - minPx) / (maxPx - minPx)
        }

    override var progress: Float by mutableFloatStateOf(0F)
        private set

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        val height = topBarHeight

        if (height == minPx) {
            if (available.y > 0F) {
                return if (contentCanScrollBackward.value) {
                    Offset.Zero
                } else {
                    topBarHeight += available.y
                    Offset(0F, available.y)
                }
            }
        }

        if (height + available.y > maxPx) {
            topBarHeight = maxPx
            return Offset(0f, maxPx - height)
        }

        if (height + available.y < minPx) {
            topBarHeight = minPx
            return Offset(0f, minPx - height)
        }

        topBarHeight += available.y

        return Offset(0f, available.y)
    }
}
