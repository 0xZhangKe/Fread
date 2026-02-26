package com.zhangke.framework.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun SingleRowTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarColors.default(),
) {
    val actionsRow: @Composable () -> Unit = {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions,
        )
    }
    Box(
        modifier = modifier.drawBehind {
            val color = colors.containerColor
            if (color != Color.Unspecified) {
                drawRect(color = color)
            }
        },
    ) {
        SingleRowTopAppBarLayout(
            modifier = Modifier.windowInsetsPadding(windowInsets).clipToBounds(),
            navigationIconContentColor = colors.navigationIconContentColor,
            titleContentColor = colors.titleContentColor,
            actionIconContentColor = colors.actionIconContentColor,
            title = title,
            titleTextStyle = ToolbarTokens.titleTextStyle,
            navigationIcon = navigationIcon,
            actions = actionsRow,
            height = TopAppBarDefaults.TopAppBarExpandedHeight,
        )
    }
}

@Stable
class TopAppBarColors(
    val containerColor: Color,
    val navigationIconContentColor: Color,
    val titleContentColor: Color,
    val actionIconContentColor: Color,
) {

    companion object {

        @Composable
        fun default(
            containerColor: Color = TopAppBarDefaults.topAppBarColors().containerColor,
            navigationIconContentColor: Color = TopAppBarDefaults.topAppBarColors().navigationIconContentColor,
            titleContentColor: Color = TopAppBarDefaults.topAppBarColors().titleContentColor,
            actionIconContentColor: Color = TopAppBarDefaults.topAppBarColors().actionIconContentColor,
        ): TopAppBarColors {
            return TopAppBarColors(
                containerColor = containerColor,
                navigationIconContentColor = navigationIconContentColor,
                titleContentColor = titleContentColor,
                actionIconContentColor = actionIconContentColor,
            )
        }
    }
}

@Composable
private fun SingleRowTopAppBarLayout(
    modifier: Modifier,
    navigationIconContentColor: Color,
    titleContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    height: Dp,
) {
    Layout(
        content = {
            Box(Modifier.layoutId("navigationIcon").padding(start = TopAppBarHorizontalPadding)) {
                CompositionLocalProvider(
                    LocalContentColor provides navigationIconContentColor,
                    content = navigationIcon,
                )
            }
            Box(
                modifier =
                    Modifier.layoutId("title").padding(horizontal = TopAppBarHorizontalPadding)
            ) {
                CompositionLocalProvider(LocalContentColor provides titleContentColor) {
                    ProvideTextStyle(titleTextStyle, content = title)
                }
            }
            Box(Modifier.layoutId("actionIcons").padding(end = TopAppBarHorizontalPadding)) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = actions,
                )
            }
        },
        modifier = modifier,
        measurePolicy = { measurables, constraints ->
            val navigationIconPlaceable = measurables.first { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0))
            val actionIconsPlaceable = measurables.first { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0))

            val maxTitleWidth = if (constraints.maxWidth == Constraints.Infinity) {
                constraints.maxWidth
            } else {
                (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width)
                    .coerceAtLeast(0)
            }
            val titlePlaceable = measurables.first { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

            val maxLayoutHeight =
                max(
                    height.roundToPx(),
                    max(
                        titlePlaceable.height,
                        max(navigationIconPlaceable.height, actionIconsPlaceable.height),
                    ),
                )

            layout(constraints.maxWidth, maxLayoutHeight) {
                navigationIconPlaceable.placeRelative(
                    x = 0,
                    y = (maxLayoutHeight - navigationIconPlaceable.height) / 2,
                )

                val start = max(TopAppBarTitleInset.roundToPx(), navigationIconPlaceable.width)
                val end = actionIconsPlaceable.width
                var titleX = Alignment.Start.align(
                    size = titlePlaceable.width,
                    space = constraints.maxWidth,
                    layoutDirection = LayoutDirection.Ltr,
                )
                if (titleX < start) {
                    titleX += (start - titleX)
                } else if (titleX + titlePlaceable.width > constraints.maxWidth - end) {
                    titleX += ((constraints.maxWidth - end) - (titleX + titlePlaceable.width))
                }

                val titleY = (maxLayoutHeight - titlePlaceable.height) / 2
                titlePlaceable.placeRelative(titleX, titleY)

                actionIconsPlaceable.placeRelative(
                    x = constraints.maxWidth - actionIconsPlaceable.width,
                    y = (maxLayoutHeight - actionIconsPlaceable.height) / 2,
                )
            }
        },
    )
}

private val TopAppBarHorizontalPadding = 4.dp
private val TopAppBarTitleInset = 16.dp - TopAppBarHorizontalPadding
