package com.zhangke.utopia.status.ui.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
internal fun TripleImageMediaLayout(
    modifier: Modifier = Modifier,
    containerWidth: Dp,
    style: BlogImageMediaStyle,
    aspectList: List<Float>,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val firstAspect = aspectList.first()
    if (firstAspect <= 1F) {
        // horizontal arrange
        HorizontalImageMediaFrameLayout(
            modifier = modifier,
            containerWidth = containerWidth,
            style = style,
            aspectList = aspectList,
            itemContent = itemContent,
        )
    } else {
        // vertical arrange
        VerticalImageMediaFrameLayout(
            modifier = modifier,
            containerWidth = containerWidth,
            style = style,
            aspectList = aspectList,
            itemContent = itemContent,
        )
    }
}
