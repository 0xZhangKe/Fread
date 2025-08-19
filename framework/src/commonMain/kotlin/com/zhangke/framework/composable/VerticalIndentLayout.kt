package com.zhangke.framework.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp

@Composable
fun VerticalIndentLayout(
    modifier: Modifier,
    indentHeight: Dp,
    headerContent: @Composable () -> Unit,
    indentContent: @Composable () -> Unit,
) {
    Layout(
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val headerPlaceable = measurables.first().measure(constraints)
            val indentPlaceable = measurables.last().measure(constraints)
            val containerHeight =
                headerPlaceable.height + indentPlaceable.height - indentHeight.roundToPx()
            layout(constraints.maxWidth, containerHeight) {
                headerPlaceable.placeRelative(0, 0)
                indentPlaceable.placeRelative(
                    x = 0,
                    y = headerPlaceable.height - indentHeight.roundToPx()
                )
            }
        },
        content = {
            headerContent()
            indentContent()
        }
    )
}
