package com.zhangke.fread.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun VerticalImageMediaListLayout(
    modifier: Modifier,
    style: BlogImageMediaStyle,
    dropFirst: Int,
    aspectList: List<Float>,
    itemContent: @Composable (index: Int) -> Unit,
) {
    Column(modifier = modifier) {
        for (index in dropFirst until aspectList.size) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F)
            ) {
                itemContent(index)
            }
            if (index < aspectList.lastIndex) {
                VerticalSpacer(height = style.verticalDivider)
            }
        }
    }
}
