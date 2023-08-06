package com.zhangke.framework.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Tab
import androidx.compose.material3.Divider
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.pxToDp

@Composable
fun UtopiaTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.containerColor,
    contentColor: Color = TabRowDefaults.contentColor,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit =
        @Composable { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
            )
        },
    divider: @Composable () -> Unit = @Composable {
        Divider()
    },
    tabs: @Composable () -> Unit
) {
    val density = LocalDensity.current
    var tabContainerWidth: Dp? by remember {
        mutableStateOf(null)
    }
    var allTabSumWidth: Dp? by remember {
        mutableStateOf(null)
    }
    var tabEdgePadding by remember {
        mutableStateOf(0.dp)
    }
    if (tabContainerWidth != null && allTabSumWidth != null) {
        val edgePadding = (tabContainerWidth!! - allTabSumWidth!!) / 2
        if (edgePadding > 0.dp) {
            tabEdgePadding = edgePadding
        }
    }
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.onGloballyPositioned {
            tabContainerWidth = it.size.width.pxToDp(density)
        },
        containerColor = containerColor,
        contentColor = contentColor,
        edgePadding = tabEdgePadding,
        indicator = { tabPositions ->
            var allWidth = 0.dp
            for (position in tabPositions) {
                allWidth += position.width
            }
            allTabSumWidth = allWidth
            indicator(tabPositions)
        },
        divider = divider,
        tabs = tabs,
    )
}

@Composable
fun UtopiaTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = TabRowDefaults.containerColor,
    contentColor: Color = TabRowDefaults.contentColor,
    indicatorContent: @Composable (tabPosition: TabPosition) -> Unit =
        @Composable {
            UtopiaTabRowDefault.Indicator()
        },
    divider: @Composable () -> Unit = @Composable {
        Divider()
    },
    tabCount: Int,
    selectedIndex: Int,
    tabContent: @Composable (index: Int) -> Unit,
    onTabClick: (index: Int) -> Unit,
) {
    val density = LocalDensity.current
    val tabContentWidth = remember {
        mutableStateMapOf<Int, Dp>()
    }
    UtopiaTabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        containerColor = containerColor,
        contentColor = contentColor,
        indicator = { tabPositions ->
            val position = tabPositions[selectedIndex]
            Column(
                modifier = Modifier
                    .ownTabIndicatorOffset(
                        currentTabPosition = position,
                        currentTabWidth = tabContentWidth[selectedTabIndex]
                            ?: position.width,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                indicatorContent(position)
                Box(modifier = Modifier.height(1.5.dp))
            }
        },
        divider = divider,
        tabs = {
            repeat(tabCount) { index ->
                Tab(
                    selected = index == selectedIndex,
                    onClick = { onTabClick(index) },
                ) {
                    Box(
                        modifier = Modifier.onGloballyPositioned {
                            tabContentWidth[index] = it.size.width.pxToDp(density)
                        }
                    ) {
                        tabContent(index)
                    }
                }
            }
        },
    )
}

object UtopiaTabRowDefault {

    val IndicatorHeight = androidx.compose.material.TabRowDefaults.IndicatorHeight

    @Composable
    fun Indicator(
        modifier: Modifier = Modifier,
        height: Dp = IndicatorHeight,
        color: Color = LocalContentColor.current,
        shape: Shape = RoundedCornerShape(2.dp),
    ) {
        Box(
            modifier
                .fillMaxWidth()
                .height(height)
                .background(
                    color = color,
                    shape = shape
                )
        )
    }
}
