package com.zhangke.framework.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.primaryContainerColor
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zhangke.framework.utils.pxToDp

@Composable
fun FreadTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = primaryContainerColor,
    indicatorContent: @Composable (tabPosition: TabPosition) -> Unit =
        @Composable {
            FreadTabRowDefault.Indicator()
        },
    divider: @Composable () -> Unit = @Composable {
        HorizontalDivider(thickness = 0.5.dp)
    },
    tabCount: Int,
    tabContent: @Composable (index: Int) -> Unit,
    onTabClick: (index: Int) -> Unit,
) {
    val density = LocalDensity.current
    val tabContentWidth = remember {
        mutableStateMapOf<Int, Dp>()
    }
    FreadTabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        containerColor = containerColor,
        indicator = { tabPositions ->
            val position = tabPositions.getOrNull(selectedTabIndex)
            if (position != null) {
                Column(
                    modifier = Modifier
                        .ownTabIndicatorOffset(
                            currentTabPosition = position,
                            currentTabWidth = tabContentWidth[selectedTabIndex] ?: 0.dp,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    indicatorContent(position)
                    Box(modifier = Modifier.height(1.5.dp))
                }
            }
        },
        divider = divider,
        tabs = {
            repeat(tabCount) { index ->
                Tab(
                    selected = index == selectedTabIndex,
                    onClick = { onTabClick(index) },
                ) {
                    Box(
                        modifier = Modifier
                            .onSizeChanged {
                                tabContentWidth[index] = it.width.pxToDp(density)
                            }
                            .padding(8.dp)
                    ) {
                        val contentColor = if (index == selectedTabIndex) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                        CompositionLocalProvider(
                            LocalContentColor provides contentColor
                        ) {
                            tabContent(index)
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun FreadTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    containerColor: Color = primaryContainerColor,
    indicator: @Composable (tabPositions: List<TabPosition>) -> Unit =
        @Composable { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
            )
        },
    divider: @Composable () -> Unit = @Composable {
        HorizontalDivider()
    },
    tabs: @Composable () -> Unit
) {
    val density = LocalDensity.current
    var tabContainerWidth: Dp? by rememberSaveable(saver = StateSaver.MutableNullableDpSaver) {
        mutableStateOf(null)
    }
    var allTabSumWidth: Dp? by rememberSaveable(saver = StateSaver.MutableNullableDpSaver) {
        mutableStateOf(null)
    }
    var tabEdgePadding by rememberSaveable(saver = StateSaver.MutableDpSaver) {
        mutableStateOf(0.dp)
    }
    if (tabContainerWidth != null && allTabSumWidth != null) {
        LaunchedEffect(tabContainerWidth, allTabSumWidth) {
            tabEdgePadding = if (tabContainerWidth!! <= allTabSumWidth!!) {
                0.dp
            } else {
                (tabContainerWidth!! - allTabSumWidth!!) / 2
            }
        }
    }
    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier.onSizeChanged {
            tabContainerWidth = it.width.pxToDp(density)
        },
        containerColor = containerColor,
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

object FreadTabRowDefault {

    private val IndicatorHeight = 3.dp

    @Composable
    fun Indicator(
        modifier: Modifier = Modifier,
        height: Dp = IndicatorHeight,
        color: Color = MaterialTheme.colorScheme.primary,
        shape: Shape = RoundedCornerShape(
            topStart = 3.dp, topEnd = 3.dp,
        ),
    ) {
        Box(
            modifier
                .fillMaxWidth()
                .height(height)
                .background(color = color, shape = shape)
        )
    }
}
