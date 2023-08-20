package com.zhangke.utopia.status.ui.image

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.zhangke.framework.ktx.second

@Composable
internal fun DoubleBlogImageLayout(
    modifier: Modifier = Modifier,
    style: BlogImageMediaStyle,
    aspectList: List<Float>,
    itemContent: @Composable (index: Int) -> Unit,
) {
    val firstAspect = aspectList.first()
    val secondAspect = aspectList.second()
    val firstFixedAspect = style.getCompliantAspect(firstAspect)
    val secondFixedAspect = style.getCompliantAspect(secondAspect)
    if (firstAspect > 1 && secondAspect > 1) {
        // vertical arrange
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(firstFixedAspect)
            ) {
                itemContent(0)
            }
            VerticalSpacer(style.verticalDivider)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(secondFixedAspect)
            ) {
                itemContent(1)
            }
        }
    } else {
        // horizontal arrange
        Row(
            modifier = modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .weight(firstFixedAspect)
                    .aspectRatio(firstFixedAspect)
            ) {
                itemContent(0)
            }
            HorizontalSpacer(style.horizontalDivider)
            Box(
                modifier = Modifier
                    .weight(secondFixedAspect)
                    .aspectRatio(secondFixedAspect)
            ) {
                itemContent(1)
            }
        }
    }
}
