package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import kotlin.math.max

@Composable
fun Grid(
    modifier: Modifier,
    columnCount: Int,
    content: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {
            var yOffset = 0
            val itemWidth = constraints.maxWidth / columnCount
            val rowCount = (measurables.size - 1) / columnCount + 1
            val itemMaxHeight = constraints.maxHeight / rowCount
            val itemConstraints = constraints.copy(maxWidth = itemWidth, maxHeight = itemMaxHeight)
            var itemHeight = -1
            measurables.forEachIndexed { index, measurable ->
                val placeable = measurable.measure(itemConstraints)
                placeable.placeRelative(
                    x = itemWidth * (index % columnCount),
                    y = yOffset,
                )
                itemHeight = max(itemHeight, placeable.height)
                if (index % columnCount == columnCount - 1) {
                    // next round is new line
                    yOffset += itemHeight
                }
            }
        }
    }
}
