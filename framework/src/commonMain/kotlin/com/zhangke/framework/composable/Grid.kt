package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun Grid(
    modifier: Modifier,
    horizontalSpacing: Dp = 0.dp,
    verticalSpacing: Dp = 0.dp,
    columnCount: Int,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val itemWidth =
            (constraints.maxWidth - horizontalSpacing.roundToPx() * (columnCount - 1)) / columnCount
        val rowCount = (measurables.size - 1) / columnCount + 1
        val itemConstraints = constraints.copy(minWidth = itemWidth, maxWidth = itemWidth)
        val placeables = measurables.map { it.measure(itemConstraints) }
        val itemTotalHeight = placeables.chunked(columnCount).sumOf { list ->
            list.maxBy { it.height }.height
        }
        val totalHeight = (rowCount - 1) * verticalSpacing.roundToPx() + itemTotalHeight
        layout(constraints.maxWidth, totalHeight) {
            var yOffset = 0
            var itemHeight = -1
            placeables.forEachIndexed { index, placeable ->
                val xSpacing = (index % columnCount) * horizontalSpacing.roundToPx()
                placeable.placeRelative(
                    x = itemWidth * (index % columnCount) + xSpacing,
                    y = yOffset,
                )
                itemHeight = max(itemHeight, placeable.height)
                if (index % columnCount == columnCount - 1) {
                    // next round is new line
                    yOffset += itemHeight
                    yOffset += verticalSpacing.roundToPx()
                }
            }
        }
    }
}
