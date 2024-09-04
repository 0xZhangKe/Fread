package com.zhangke.fread.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
internal fun QuadrupleImageMediaLayout(
    modifier: Modifier = Modifier,
    containerWidth: Dp,
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val firstAspect = aspectList.first()
    if (firstAspect > style.quadrupleHorizontalThreshold &&
        firstAspect < style.quadrupleVerticalThreshold
    ) {
        // Grid arrange
        QuadrupleGridLayout(
            modifier = modifier,
            aspectList = aspectList,
            style = style,
            itemContent = itemContent,
        )
    } else if (firstAspect <= style.quadrupleHorizontalThreshold) {
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

@Composable
private fun QuadrupleGridLayout(
    modifier: Modifier,
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val firstAspect = aspectList.first()
    val fixedFirstAspect = style.getCompliantAspect(firstAspect)
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .aspectRatio(fixedFirstAspect)
            ) {
                itemContent(0)
            }
            HorizontalSpacer(width = style.horizontalDivider)
            Box(
                modifier = Modifier
                    .weight(1F)
                    .aspectRatio(fixedFirstAspect)
            ) {
                itemContent(1)
            }
        }
        VerticalSpacer(height = style.verticalDivider)
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .aspectRatio(fixedFirstAspect)
            ) {
                itemContent(2)
            }
            HorizontalSpacer(width = style.horizontalDivider)
            Box(
                modifier = Modifier
                    .weight(1F)
                    .aspectRatio(fixedFirstAspect)
            ) {
                itemContent(3)
            }
        }
    }
}
