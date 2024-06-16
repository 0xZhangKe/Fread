package com.zhangke.fread.activitypub.app.internal.screen.status.post.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.FirstBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
internal fun TwoTextsInRow(
    firstText: @Composable () -> Unit,
    secondText: @Composable () -> Unit,
    spacing: Dp,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val spacingPx = with(density) { spacing.roundToPx().toFloat() }

    Box(modifier = modifier) {
        Layout(
            content = {
                firstText()
                secondText()
            },
            measurePolicy = { measurables, constraints ->
                val secondTextMeasurable = measurables[1]
                val secondTextPlaceable = secondTextMeasurable.measure(
                    constraints.copy(maxWidth = constraints.maxWidth - spacingPx.toInt())
                )

                val firstTextMeasurable = measurables[0]
                val firstTextPlaceable = firstTextMeasurable.measure(
                    constraints.copy(maxWidth = constraints.maxWidth - spacingPx.toInt() - secondTextPlaceable.width)
                )
                val firstBaseLine = firstTextPlaceable[FirstBaseline]
                val secondBaseLine = secondTextPlaceable[FirstBaseline]

                val secondTextHeight = secondTextPlaceable.height
                layout(
                    width = constraints.maxWidth,
                    height = maxOf(firstTextPlaceable.height, secondTextHeight)
                ) {
                    firstTextPlaceable.place(
                        x = 0,
                        y = 0,
                    )
                    secondTextPlaceable.place(
                        x = firstTextPlaceable.width + spacingPx.toInt(),
                        y = firstBaseLine - secondBaseLine,
                    )
                }
            }
        )
    }
}
