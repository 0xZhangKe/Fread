package com.zhangke.fread.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtMost
import com.zhangke.framework.ktx.averageDropFirst

/**
 * As shown below:
 *       ---------------------
 *      ｜                    ｜
 *      ｜                    ｜
 *      ｜                    ｜
 *      ｜                    ｜
 *      ｜                    ｜
 *      ｜                    ｜
 *       ---------------------
 *       ---------------------
 *      ｜                    ｜
 *       ---------------------
 */
@Composable
internal fun VerticalImageMediaFrameLayout(
    modifier: Modifier,
    containerWidth: Dp,
    style: BlogImageMediaStyle,
    topAspect: Float,
    bottomAspect: Float,
    fixedHeight: Boolean = false,
    topContent: @Composable () -> Unit,
    bottomContent: @Composable () -> Unit,
) {
    val fixedTopAspect = style.getCompliantAspect(topAspect)
    val fixedBottomAspect = style.getCompliantAspect(bottomAspect)
    val finalModifier = if (fixedHeight) {
        modifier.fillMaxHeight()
    } else {
        val minHeight = containerWidth / style.maxAspect
        val maxHeight = containerWidth / style.minAspect
        val bottomHeight = containerWidth / fixedBottomAspect
        val topHeight = containerWidth / fixedTopAspect
        val reckonTotalHeight = style.verticalDivider + bottomHeight + topHeight
        val totalHeight = reckonTotalHeight.coerceAtLeast(minHeight).coerceAtMost(maxHeight)
        modifier.height(totalHeight)
    }
    Column(
        modifier = finalModifier
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F / fixedTopAspect)
        ) {
            topContent()
        }
        VerticalSpacer(height = style.verticalDivider)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F / fixedBottomAspect)
        ) {
            bottomContent()
        }
    }
}

/**
 * As shown below:
 *       ---------------------
 *      ｜                    ｜
 *      ｜                    ｜
 *      ｜                    ｜
 *      ｜                    ｜
 *      ｜                    ｜
 *      ｜                    ｜
 *       ---------------------
 *       ----    ------   -----
 *      ｜   ｜  ｜    ｜  ｜   ｜
 *       ----    ------   -----
 */
@Composable
internal fun VerticalImageMediaFrameLayout(
    modifier: Modifier,
    containerWidth: Dp,
    style: BlogImageMediaStyle,
    aspectList: List<Float>,
    itemContent: @Composable (index: Int) -> Unit,
) {
    VerticalImageMediaFrameLayout(
        modifier = modifier,
        containerWidth = containerWidth,
        style = style,
        topAspect = aspectList.first(),
        bottomAspect = aspectList.averageDropFirst(1).toFloat(),
        topContent = {
            itemContent(0)
        },
        bottomContent = {
            HorizontalImageMediaListLayout(
                modifier = Modifier,
                style = style,
                dropFirst = 1,
                aspectList = aspectList,
                itemContent = itemContent,
            )
        }
    )
}
