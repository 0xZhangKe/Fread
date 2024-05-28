package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.ktx.second
import com.zhangke.framework.utils.toPx

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithTabLayoutOld(
    topBarContent: @Composable BoxScope.() -> Unit,
    tabContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    topBarHeight: Dp = 64.dp,
    tabHeight: Dp = 42.dp,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    scrollableContent: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    val topBarHeightPx = topBarHeight.toPx().toInt()
    val tabHeightPx = tabHeight.toPx()
    val statusBarHeight = windowInsets.getTop(density)
    val totalHeight = (topBarHeight + tabHeight).toPx() + statusBarHeight
    val tabBarYOffsetRange = IntRange(-totalHeight.toInt(), topBarHeightPx)
    val topBarYOffsetRange = IntRange(-(statusBarHeight + topBarHeightPx), statusBarHeight)
    val nestedScrollConnection = rememberCollapsableTopBarScrollConnection(
        minPx = 0F,
        maxPx = totalHeight,
    )
    val progress by rememberUpdatedState(newValue = nestedScrollConnection.progress)
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
                        .height(topBarHeight),
                ) {
                    topBarContent()
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
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
                    val processedOffset = totalHeight * progress
                    val tabBarYOffset = -tabBarYOffsetRange.progress(progress)
                    tabBarPlaceable.placeRelative(0, tabBarYOffset)
                    var topBarYOffset = 0
                    if ((-tabBarYOffset) > tabHeightPx){
                        topBarYOffset = (processedOffset.toInt() - tabHeightPx.toInt()).coerceIn(topBarYOffsetRange)
                    }
                    topBarPlaceable.placeRelative(0, topBarYOffset)
                    scrollableContentPlaceable.placeRelative(
                        0,
                        totalHeight.toInt() - processedOffset.toInt() + statusBarHeight
                    )
                }
            },
        )
    }
}

private fun IntRange.progress(progress: Float): Int {
    return (first + (endInclusive - first) * progress).toInt()
}
