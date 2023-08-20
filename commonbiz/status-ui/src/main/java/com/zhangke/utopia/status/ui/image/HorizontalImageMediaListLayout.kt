package com.zhangke.utopia.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun HorizontalImageMediaListLayout(
    modifier: Modifier,
    style: BlogImageMediaStyle,
    dropFirst: Int,
    aspectList: List<Float>,
    itemContent: @Composable (index: Int) -> Unit,
) {
    Row(
        modifier = modifier
    ) {
        for (index in dropFirst until aspectList.size) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1F)
            ) {
                itemContent(index)
            }
            if (index < aspectList.lastIndex) {
                HorizontalSpacer(width = style.horizontalDivider)
            }
        }
    }
}
