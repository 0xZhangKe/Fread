package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.ktx.second
import com.zhangke.framework.utils.toPx

@Composable
fun TopBarWithTabLayout(
    topBarContent: @Composable BoxScope.() -> Unit,
    tabContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    topBarHeight: Dp = 64.dp,
    tabHeight: Dp = 42.dp,
    scrollableContent: @Composable BoxScope.() -> Unit,
) {
    val topBarHeightPx = topBarHeight.toPx()
    val tabHeightPx = tabHeight.toPx()
    val nestedScrollConnection = rememberCollapsableTopBarScrollConnection(
        minPx = 0F,
        maxPx = (topBarHeight + tabHeight).toPx(),
    )
    val process by rememberUpdatedState(newValue = nestedScrollConnection.progress)
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Layout(
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topBarHeight)
                        .invisibleByProcess(process),
                ) {
                    topBarContent()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .invisibleByProcess(process),
                ) {
                    tabContent()
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
                val tabBarPlaceable = measurables.second().measure(constraints)
                val scrollableContentPlaceable = measurables[2].measure(constraints)
                layout(constraints.maxWidth, constraints.maxHeight) {
                    val totalHeaderHeight = topBarHeightPx + tabHeightPx
                    val processedOffset = totalHeaderHeight * process
                    val tabBarYOffset = topBarHeightPx - processedOffset.coerceIn(0F, tabHeightPx)
                    tabBarPlaceable.placeRelative(0, tabBarYOffset.toInt())
                    val topBarYOffset = -((processedOffset - tabHeightPx).coerceIn(0F, tabHeightPx))
                    topBarPlaceable.placeRelative(0, topBarYOffset.toInt())
                    scrollableContentPlaceable.placeRelative(
                        0,
                        totalHeaderHeight.toInt() - processedOffset.toInt()
                    )
                }
            },
        )
    }
}

private fun Modifier.invisibleByProcess(process: Float): Modifier {
    return Modifier.alpha(1F - process) then this
}
