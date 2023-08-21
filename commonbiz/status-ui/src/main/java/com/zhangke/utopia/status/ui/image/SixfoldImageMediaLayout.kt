package com.zhangke.utopia.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun SixfoldImageMediaLayout(
    modifier: Modifier = Modifier,
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(style.sixfoldAspect)
    ) {
        HorizontalImageMediaList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            dropFirst = 0,
            aspectList = aspectList.take(3),
            style = style,
            itemContent = itemContent,
        )
        VerticalSpacer(height = style.verticalDivider)
        HorizontalImageMediaList(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            dropFirst = 3,
            aspectList = aspectList,
            style = style,
            itemContent = itemContent,
        )
    }
}

@Composable
private fun HorizontalImageMediaList(
    modifier: Modifier,
    dropFirst: Int,
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    Row(modifier = modifier) {
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
