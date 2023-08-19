package com.zhangke.utopia.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import com.zhangke.framework.ktx.second

@Composable
internal fun QuadrupleImageMediaLayout(
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
            aspectList = aspectList,
            style = style,
            itemContent = itemContent,
        )
    } else if (firstAspect <= style.quadrupleHorizontalThreshold) {
        // horizontal arrange
        HorizontalLayout(
            containerWidth = containerWidth,
            aspectList = aspectList,
            style = style,
            itemContent = itemContent,
        )
    } else {
        // vertical arrange
        VerticalLayout(
            aspectList = aspectList,
            style = style,
            itemContent = itemContent,
        )
    }
}

@Composable
private fun QuadrupleGridLayout(
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val firstAspect = aspectList.first()
    val fixedFirstAspect = style.getCompliantAspect(firstAspect)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(style.radius))
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

@Composable
private fun HorizontalLayout(
    containerWidth: Dp,
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val firstAspect = aspectList.first()
    val fixedFirstAspect = style.getCompliantAspect(firstAspect)
    val remainderContainerWidth = containerWidth - style.horizontalDivider
    val firstImageWidthWeight = style.decideFirstImageWeightInHorizontalMode(firstAspect)
    val firstImageWidth = remainderContainerWidth * firstImageWidthWeight
    val firstImageHeight = firstImageWidth / fixedFirstAspect
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(firstImageHeight)
            .clip(RoundedCornerShape(style.radius))
    ) {
        Box(modifier = Modifier.size(width = firstImageWidth, height = firstImageHeight)) {
            itemContent(0)
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(
                    width = remainderContainerWidth - firstImageWidth,
                    height = firstImageHeight,
                )
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    itemContent(1)
                }
                VerticalSpacer(height = style.verticalDivider)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    itemContent(2)
                }
                VerticalSpacer(height = style.verticalDivider)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1F)
                ) {
                    itemContent(3)
                }
            }
        }
    }
}

@Composable
private fun VerticalLayout(
    aspectList: List<Float>,
    style: BlogImageMediaStyle,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val firstAspect = aspectList.first()
    val fixedFirstAspect = style.getCompliantAspect(firstAspect)
    val secondAspect = style.getCompliantAspect(aspectList.second())
    val secondFixedAspect = style.getCompliantAspect(secondAspect)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(style.radius))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(fixedFirstAspect)
        ) {
            itemContent(0)
        }
        VerticalSpacer(height = style.verticalDivider)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(secondFixedAspect)
        ) {
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            ) {
                itemContent(1)
            }
            HorizontalSpacer(width = style.horizontalDivider)
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            ) {
                itemContent(2)
            }
            HorizontalSpacer(width = style.horizontalDivider)
            Box(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxHeight()
            ) {
                itemContent(3)
            }
        }
    }
}
