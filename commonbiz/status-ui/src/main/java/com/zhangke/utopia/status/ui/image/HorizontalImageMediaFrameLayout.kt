package com.zhangke.utopia.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

/**
 * As shown below:
 *       ---------------   --------
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *       ---------------   ---------
 */
@Composable
internal fun HorizontalImageMediaFrameLayout(
    modifier: Modifier,
    containerWidth: Dp,
    fixedHeight: Boolean = false,
    style: BlogImageMediaStyle,
    startAspect: Float,
    startContent: @Composable () -> Unit,
    endContent: @Composable () -> Unit,
) {
    val startFixedAspect = style.getCompliantAspect(startAspect)
    val firstImageWidthWeight = style.decideFirstImageWeightInHorizontalMode(startFixedAspect)
    val remainderContainerWidth = containerWidth - style.horizontalDivider
    val firstImageWidth = remainderContainerWidth * firstImageWidthWeight
    val finalModifier = if (fixedHeight) {
        modifier.fillMaxHeight()
    } else {
        val firstImageHeight = firstImageWidth / startFixedAspect
        modifier.height(firstImageHeight)
    }
    Box(
        modifier = finalModifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .width(firstImageWidth)
                .fillMaxHeight()
        ) {
            startContent()
        }
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(remainderContainerWidth - firstImageWidth)
                .fillMaxHeight()
        ) {
            endContent()
        }
    }
}

/**
 * As shown below:
 *       ---------------   --------
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜  --------
 *      ｜              ｜  --------
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *      ｜              ｜ ｜       ｜
 *       ---------------   ---------
 */
@Composable
internal fun HorizontalImageMediaFrameLayout(
    modifier: Modifier,
    containerWidth: Dp,
    style: BlogImageMediaStyle,
    aspectList: List<Float>,
    itemContent: @Composable (index: Int) -> Unit,
) {
    HorizontalImageMediaFrameLayout(
        modifier = modifier,
        containerWidth = containerWidth,
        style = style,
        startAspect = aspectList.first(),
        startContent = {
            itemContent(0)
        },
        endContent = {
            VerticalImageMediaListLayout(
                modifier = Modifier,
                dropFirst = 1,
                style = style,
                aspectList = aspectList,
                itemContent = itemContent,
            )
        }
    )
}
