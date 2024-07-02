package com.zhangke.framework.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
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
import com.zhangke.framework.utils.pxToDp
import com.zhangke.framework.utils.toPx

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithTabLayout(
    topBarContent: @Composable BoxScope.() -> Unit,
    tabContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    tabHeight: Dp = 42.dp,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollableContent: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    val statusBarHeight = windowInsets.getTop(density).pxToDp(density)
    val realTopBarHeight = 64.dp + statusBarHeight
    val topBarHeightPx = realTopBarHeight.toPx()
    val tabHeightPx = tabHeight.toPx()
    val nestedScrollConnection = rememberCollapsableTopBarScrollConnection(
        minPx = 0F,
        maxPx = (realTopBarHeight + tabHeight).toPx(),
    )
    val process by rememberUpdatedState(newValue = nestedScrollConnection.progress)
    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
    ) {
        Layout(
            modifier = Modifier,
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(realTopBarHeight),
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
        if (statusBarHeight > 0.dp) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(statusBarHeight)
                    .background(colors.containerColor)
            )
        }
    }
}
