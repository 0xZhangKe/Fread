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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.zhangke.framework.ktx.second
import com.zhangke.framework.utils.pxToDp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarWithTabLayout(
    topBarContent: @Composable BoxScope.() -> Unit,
    tabContent: @Composable BoxScope.() -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    scrollableContent: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    var topBarHeightInPx: Int by rememberSaveable { mutableIntStateOf(0) }
    var tabHeightInPx: Int by rememberSaveable { mutableIntStateOf(0) }
    val nestedScrollConnection = rememberCollapsableTopBarScrollConnection(
        minPx = 0F,
        maxPx = (topBarHeightInPx + tabHeightInPx).toFloat(),
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
                        .onSizeChanged { topBarHeightInPx = it.height },
                ) {
                    topBarContent()
                }
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .onSizeChanged { tabHeightInPx = it.height },
                ) {
                    tabContent()
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    scrollableContent()
                }
            },
            measurePolicy = { measurables, constraints ->
                val topBarPlaceable = measurables.first().measure(constraints)
                val tabBarPlaceable = measurables.second().measure(constraints)
                val scrollableContentPlaceable = measurables[2].measure(constraints)
                layout(constraints.maxWidth, constraints.maxHeight) {
                    val totalHeaderHeight = topBarHeightInPx + tabHeightInPx
                    val processedOffset = totalHeaderHeight * process
                    val tabBarYOffset = topBarHeightInPx - processedOffset.coerceAtLeast(0F)
                    tabBarPlaceable.placeRelative(0, tabBarYOffset.toInt())
                    val topBarYOffset = -((processedOffset - tabHeightInPx).coerceAtLeast(0F))
                    topBarPlaceable.placeRelative(0, topBarYOffset.toInt())
                    scrollableContentPlaceable.placeRelative(
                        0,
                        totalHeaderHeight - processedOffset.toInt()
                    )
                }
            },
        )
//        val statusBarHeight = windowInsets.getTop(density).pxToDp(density)
//        if (statusBarHeight > 0.dp) {
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(statusBarHeight)
//                    .background(colors.containerColor)
//            )
//        }
    }
}
